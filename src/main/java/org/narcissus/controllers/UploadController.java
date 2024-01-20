package org.narcissus.controllers;

import org.narcissus.DTO.RequestDTO;
import org.narcissus.services.OCR.Tesseract;
import org.narcissus.services.python.PythonMapper;
import org.narcissus.services.upload.ConcurrentUploadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/")
public class UploadController {

    Logger logger = LoggerFactory.getLogger(UploadController.class);
    private static final String excelSheetMimeType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    private ConcurrentUploadService concurrentUploadService;
    private Tesseract tesseract;

    @Autowired
    public void setUploadService(ConcurrentUploadService concurrentUploadService, Tesseract tesseract) {
        this.concurrentUploadService = concurrentUploadService;
        this.tesseract = tesseract;
    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(Model model, @NonNull @RequestParam("fileInput") Collection<MultipartFile> files) {
        logger.info("New request with {} files", files.size());
        if (!thisRequestCompliesWithRules(files)) {
            //413 Payload Too Large
            return ResponseEntity.status(413).build();
        }
        String uuid = UUID.randomUUID().toString().substring(1, 8);

        //Save new pictures to disk
        Optional<String> designatedDirectory = concurrentUploadService.uploadFiles(new RequestDTO(uuid, files.stream(), files.size()));
        //Generate .txt files for each picture
        designatedDirectory.ifPresent(directory -> tesseract.scanText(new File(directory)));
        //Call Python script and generate excel file
        Thread pythonThread = new Thread();
        pythonThread.join();
        PythonMapper.of(designatedDirectory.get(), uuid).run();

        //TODO introduce environment variable for the main upload dir
        Path excelFile = Arrays.stream(new File("/home/user/ITE/uploads/" + uuid).listFiles())
                .toList()
                .stream()
                .filter(file -> file.getName().contains("xlsx"))
                .toList()
                .get(0)
                .toPath();

        byte[] fileContent;

        try {
            fileContent = Files.readAllBytes(excelFile);
            //TODO delete whole directory instead of just 1 file
            Files.delete(excelFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        logger.info("Returning the generated excel file.");
        return ResponseEntity.ok()
                .contentType(MediaType.valueOf(excelSheetMimeType))
                .body(fileContent);
    }

    private boolean thisRequestCompliesWithRules(Collection<MultipartFile> files) {
        //Each file must be under 5Mb size and of extension of .jpg .tiff or .png
        if (files.size() <= 100) {
            return files.stream().anyMatch(file -> (file.getSize() < 5_242_880) & (
                    file.getOriginalFilename().endsWith(".jpg") ||
                            file.getOriginalFilename().endsWith(".png") ||
                            file.getOriginalFilename().endsWith(".tiff"))
            );
        }
        return false;
    }
}
