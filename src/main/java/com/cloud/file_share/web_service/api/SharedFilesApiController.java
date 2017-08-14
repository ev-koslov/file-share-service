package com.cloud.file_share.web_service.api;

import com.cloud.common.blo.file_share.SharedFile;
import com.cloud.common.blo.file_share.SharedRecord;
import com.cloud.file_share.blo.SharedFileMetadata;
import com.cloud.file_share.share_service.FileShareService;
import com.cloud.file_share.web_service.vo.DownloadableSharedFile;
import com.cloud.file_share.web_service.vo.RecordInfoWithFiles;
import com.cloud.file_share.web_service.vo.populator.RecordInfoPopulator;
import org.apache.tomcat.util.http.fileupload.FileItemIterator;
import org.apache.tomcat.util.http.fileupload.FileItemStream;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/api/")
public class SharedFilesApiController {
    private final FileShareService fileShareService;

    @Autowired
    public SharedFilesApiController(FileShareService fileShareService) {
        this.fileShareService = fileShareService;
    }

    @RequestMapping(value = "/files.verify", method = RequestMethod.POST)
    public String verifyFile(@RequestBody SharedFile file) throws IOException {

        SharedFileMetadata sharedFileMetadata = fileShareService.prepareUpload(file);

        //TODO: convert and return fileUploadInfoDTO
        return sharedFileMetadata.getMetadataId();
    }


    @RequestMapping(value = "/files.upload", method = RequestMethod.POST, produces = "application/json")
    public DownloadableSharedFile storeFile(HttpServletRequest request) throws IOException, FileUploadException {

        //using Apache FileUpload to iterate request items
        FileItemIterator itemIterator = new ServletFileUpload().getItemIterator(request);

        String metadataId = null;
        FileItemStream fileItemStream = null;

        while (itemIterator.hasNext()) {
            FileItemStream currentItem = itemIterator.next();

            if (currentItem.isFormField() && currentItem.getFieldName().equals("metadataId")) {
                InputStream inputStream = currentItem.openStream();
                byte[] data = new byte[inputStream.available()];
                inputStream.read(data);
                metadataId = new String(data);
                inputStream.close();
                continue;
            }

            //if item is not field
            if (!currentItem.isFormField() && currentItem.getFieldName().equals("file")) {
                fileItemStream = currentItem;
                break;
            }
        }

        //saving file
        SharedFile file = fileShareService.processUpload(metadataId, fileItemStream.openStream());

        DownloadableSharedFile fileInfo = new DownloadableSharedFile();

        fileInfo.setId(file.getId());
        fileInfo.setRecordId(file.getRecordId());
        fileInfo.setOriginalName(file.getOriginalName());
        fileInfo.setMimeType(file.getMimeType());
        fileInfo.setSize(file.getSize());
        fileInfo.setDownloadLink(String.format("/api/files.download?recordId=%s&fileIds=%d", file.getRecordId(), file.getId()));

        return fileInfo;
    }

    /**
     * This method allows to download files from server using record ID.
     *
     * @param recordId ID to retrieve files from
     * @param fileIds  list of files IDs
     * @param response
     * @throws IOException
     */
    @RequestMapping(value = "files.download", method = RequestMethod.GET)
    public void downloadFiles(HttpServletResponse response, @RequestParam String recordId, @RequestParam long... fileIds) throws IOException {

        List<SharedFile> files = new ArrayList<>();

        //retrieving list of files using given params
        for (long fileId : fileIds) {
            files.add(fileShareService.loadFile(recordId, fileId));
        }

        switch (files.size()) {
            //if there are no files in collection
            case 0: {
                response.sendError(404);
                break;
            }

            //if there was only one file selected
            case 1: {
                SharedFile file = files.get(0);

                String contentDispositionHeader = "attachment; filename=\"" + URLEncoder.encode(file.getOriginalName(), "UTF-8").replaceAll("\\+", "%20") + "\"";

                response.setHeader(HttpHeaders.CONTENT_DISPOSITION, contentDispositionHeader);
                response.setContentType(file.getMimeType());
                response.setContentLengthLong(file.getSize());

                IOUtils.copy(file.getDataStream(), response.getOutputStream());
                file.getDataStream().close();
                break;
            }

            //if there are more than one file selected
            default: {
                response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"selected.zip\"");
                response.setContentType("application/zip");

                ZipOutputStream zos = null;
                try {
                    zos = new ZipOutputStream(new BufferedOutputStream(response.getOutputStream()));

                    for (SharedFile file : files) {
                        ZipEntry entry = new ZipEntry(file.getOriginalName());

                        zos.putNextEntry(entry);

                        IOUtils.copy(file.getDataStream(), zos);

                        zos.closeEntry();
                        file.getDataStream().close();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    throw e;

                } finally {
                    if (zos != null) {
                        zos.close();
                    }
                }

                break;
            }
        }
    }

    @RequestMapping(value = "files.delete", method = RequestMethod.POST)
    public RecordInfoWithFiles deleteFiles(@RequestParam String recordId, @RequestParam long... fileIds) throws IOException {

        for (long fileId : fileIds) {
            fileShareService.detachFileFromRecord(recordId, fileId);
        }

        SharedRecord record = fileShareService.get(recordId);

        RecordInfoWithFiles recordInfo = new RecordInfoWithFiles();

        RecordInfoPopulator.populateRecordInfoWithFiles(record, recordInfo);

        return recordInfo;
    }
}
