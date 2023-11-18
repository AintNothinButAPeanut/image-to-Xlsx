package org.narcissus.controllers;

import org.narcissus.services.web.UploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;

@Controller
@RequestMapping("/")
public class UploadController {

    Logger logger = LoggerFactory.getLogger(UploadController.class);
    private UploadService uploadService;

    @Value("${ite.excelsDir}")
    private String iteExcel;

    @Autowired
    public void setUploadService(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(Model model, @NonNull @RequestParam("fileInput") Collection<MultipartFile> files) {
//        logger.debug("/upload controller called");
//        try {
//            if (!files.isEmpty())
//                uploadService.uploadFiles(files);
//        } catch (IOException exception) {
//            logger.debug("Failed to upload files to the server storage.");
//        }

        File[] excelFiles = new File(iteExcel).listFiles();
        byte[] fileContent = new byte[0];

        try {
            fileContent = Files.readAllBytes(excelFiles[0].toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(fileContent);

    }

}
