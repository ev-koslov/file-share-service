package com.cloud.file_share.web_service.web;

import com.cloud.common.blo.file_share.SharedRecord;
import com.cloud.file_share.share_service.FileShareService;
import com.cloud.file_share.web_service.vo.RecordInfoWithFiles;
import com.cloud.file_share.web_service.vo.populator.RecordInfoPopulator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.TimeUnit;


@Controller
@RequestMapping("/")
public class SharedRecordWebController {
    private final FileShareService fileShareService;
    private final ObjectMapper mapper;

    @Autowired
    public SharedRecordWebController(FileShareService fileShareService) {
        this.fileShareService = fileShareService;
        this.mapper = new ObjectMapper();
    }

    @RequestMapping(method = RequestMethod.GET)
    public String mainWebPage() {
        return "main_page";
    }

    @RequestMapping(value = "/create", method = RequestMethod.GET)
    public String createNewRecord(@RequestParam(value = "ttl", required = false) Long ttl, HttpServletRequest request) {
        SharedRecord record = fileShareService.create(ttl != null
                ? ttl
                : TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS)
        );

        Set<String> ownedRecords;
        HttpSession session = request.getSession();

        if (session.getAttribute("ownedRecords") == null) {
            ownedRecords = new HashSet<>();
            session.setAttribute("ownedRecords", ownedRecords);
        } else {
            ownedRecords = (Set<String>) session.getAttribute("ownedRecords");
        }

        ownedRecords.add(record.getId());

        return "redirect:/edit/" + record.getId();
    }

    @RequestMapping(value = {"/edit/{recordId}", "/view/{recordId}"}, method = RequestMethod.GET)
    public String recordPage(@PathVariable("recordId") String recordId,
                                 Model model, HttpServletRequest request) throws IOException {



        SharedRecord record;

        try {
            //retrieving record from service
            record = fileShareService.get(recordId);
        } catch (NoSuchElementException e){
            //if we have no record, redirecting to root page
            return "redirect:/";
        }

        //instantiating View Object
        RecordInfoWithFiles recordInfo = new RecordInfoWithFiles();

        //populating fields
        RecordInfoPopulator.populateRecordInfoWithFiles(record, recordInfo);

        model.addAttribute("recordJSON", mapper.writeValueAsString(recordInfo));



//        //processing edit page request
//        if (request.getServletPath().matches("\\/edit\\/")) {
//            Set<String> ownedRecords = (Set<String>) request.getSession().getAttribute("ownedRecords");
//
//            if (ownedRecords == null || !ownedRecords.contains(recordId)) {
//                viewName = "record_page_view";
//            }
//        }

        if (request.getServletPath().contains("/edit/")) {
            return "record_page_edit";
        } else {
            return "record_page_view";
        }
    }
}
