package org.narcissus.controllers;

import org.narcissus.services.web.UploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collection;

@Controller
@RequestMapping("/")
public class UploadController {

    Logger logger = LoggerFactory.getLogger(UploadController.class);
    private UploadService uploadService;

    @Autowired
    public void setUploadService(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    //TODO можно просто возвращать файл, готовый для скачивания
    @PostMapping("/upload")
    public String upload(Model model, @NonNull @RequestParam("fileInput") Collection<MultipartFile> files) {
        logger.debug("/upload controller called");
        try {
            if (!files.isEmpty())
                uploadService.uploadFiles(files);
        } catch (IOException exception) {
            logger.debug("Failed to upload files to the server storage.");
        }
        return "index.html";
    }

//    @GetMapping
//    public MultipartFile returnExcelSheet(String filePath) {
//        return new MultipartFile(filePath);
//
//    }

}
