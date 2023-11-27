package org.narcissus.controllers;

import org.narcissus.DTO.RequestDTO;
import org.narcissus.services.local.UploadService;
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
import java.util.UUID;

@Controller
@RequestMapping("/")
public class UploadController {

    Logger logger = LoggerFactory.getLogger(UploadController.class);
    @Value("${ite.excelsDir}")
    private String iteExcel;
    private static final String excelSheetMimeType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private UploadService uploadService;

    @Autowired
    public void setUploadService(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(Model model, @NonNull @RequestParam("fileInput") Collection<MultipartFile> files) {
        logger.info("Controller '/upload' called.");
        String uuid = UUID.randomUUID().toString().substring(1, 8);
        try {
            if (!files.isEmpty())
                uploadService.uploadFiles(new RequestDTO(uuid, files, (short) files.size()));
        } catch (IOException exception) {
            logger.debug("Failed to upload files to the server storage.");
        }

//        waitForExcel();
//        File excelFile = Arrays.stream(Objects.requireNonNull(new File(iteExcel)
//                        .listFiles()))
//                .filter(file -> file.toString().contains(uuid)).toList().get(0);
        File excelFile = waitForExcel();
        byte[] fileContent;

        try {
            fileContent = Files.readAllBytes(excelFile.toPath());
            Files.delete(excelFile.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(excelSheetMimeType))
                .body(fileContent);
    }

    private File waitForExcel() {
        File dir = new File(iteExcel);
        for (; ; ) {
            if (dir.listFiles().length != 0) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                return dir.listFiles()[0];
            }
        }
    }


}
