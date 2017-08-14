package com.cloud.file_share.web_service.api;

import com.cloud.common.blo.file_share.SharedRecord;
import com.cloud.file_share.share_service.FileShareService;
import com.cloud.file_share.web_service.vo.RecordInfo;
import com.cloud.file_share.web_service.vo.RecordInfoWithFiles;
import com.cloud.file_share.web_service.vo.populator.RecordInfoPopulator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/")
public class SharedRecordsApiController {
    private final FileShareService fileShareService;

    @Autowired
    public SharedRecordsApiController(FileShareService fileShareService) {
        this.fileShareService = fileShareService;
    }

    /**
     * Creates {@link SharedRecord} instance with specified time-to-live value.
     *
     * @param ttl Time until record expires
     * @return newly created RecordEntity instance
     */
    @RequestMapping(value = "records.create", method = RequestMethod.GET)
    public RecordInfo createRecord(@RequestParam(required = false) Long ttl) {
        SharedRecord record = fileShareService.create(ttl != null
                ? ttl
                : TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS));

        RecordInfo recordInfo = new RecordInfo();

        RecordInfoPopulator.populateRecordInfo(record, recordInfo);

        return recordInfo;

    }

    /**
     * Retrieves persisted {@link SharedRecord} instance by its ID
     *
     * @param recordId ID of record to retrieve
     * @return retrieved RecordEntity instance
     */
    @RequestMapping(value = "records.get", method = RequestMethod.GET)
    public RecordInfoWithFiles getRecord(@RequestParam String recordId) {
        SharedRecord record = fileShareService.get(recordId);

        RecordInfoWithFiles recordInfo = new RecordInfoWithFiles();

        RecordInfoPopulator.populateRecordInfoWithFiles(record, recordInfo);

        return recordInfo;
    }

    @RequestMapping(value = "record.update", method = RequestMethod.POST)
    public RecordInfo updateRecord(@RequestBody SharedRecord record){
        fileShareService.update(record);

        RecordInfo recordInfo = new RecordInfo();

        RecordInfoPopulator.populateRecordInfo(record, recordInfo);

        return recordInfo;
    }
}
