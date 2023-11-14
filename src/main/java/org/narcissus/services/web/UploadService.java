package org.narcissus.services.web;

import org.narcissus.exceptions.ITEUploadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

/**
 * Instances of this class take control when a new web request is made.
 * <p>Files that come from the request are saved as follows (Linux file system only):</p>
 * <em><b>'/home/ITE/uploads'</b></em> <br>
 * From there they are passed to OCR.
 *
 * @since 1.0
 */
@Service
public class UploadService {

    Logger logger = LoggerFactory.getLogger(UploadService.class);
    @Value("${ite.uploadsDir}")
    private String iteUploadsDir;
    @Value("${ite.OCRDir}")
    private String iteOcrDir;
    @Value("${ite.prefix}")
    private String itePrefix;
    private String uploadDirectory;

    public void uploadFiles(Collection<MultipartFile> files) throws IOException {
        if (!complyWithUploadRules(files)) {
            logger.info("Request made at " + new Date() + " does not meet the requirements");
            return;
        }

        uploadDirectory = createUploadDirectory();
        files.forEach(this::writeFiles);

        int filesWritten = new File(uploadDirectory).listFiles().length;

        if (files.size() != filesWritten) {
            throw new ITEUploadException("Some files were not written to the disk for unknown reason.\n"
                    + filesWritten + " files written out of " + files.size(), new RuntimeException());
        } else {
            logger.info("Downloaded and written to the disk " + filesWritten + " out of " + files.size());
        }

        moveDirectory(Paths.get(uploadDirectory));
    }


    private String createUploadDirectory() {
        String uuid = UUID.randomUUID().toString().substring(1, 8);
        String path = iteUploadsDir + "/" + itePrefix + uuid;
        if (new File(path).mkdir())
            return path;
        else throw new ITEUploadException("Failed to create directory", new RuntimeException());
    }

    private void moveDirectory(Path directory) {
        try {
            Files.move(directory, Paths.get(iteOcrDir + "/" + directory.getFileName()));
        } catch (IOException e) {
            throw new ITEUploadException("Failed to move a directory with written files to a directory with WatcherService", e);
        }

    }

    private void writeFiles(MultipartFile file) {
        try {
            Files.write(Paths.get(uploadDirectory, file.getOriginalFilename()),
                    file.getBytes());
        } catch (IOException e) {
            throw new ITEUploadException("Failed to write files.", e);
        }
    }

    private boolean complyWithUploadRules(Collection<MultipartFile> files) {
        //5_242_880 = 5Mb
        if (files.size() <= 100)
            return files.stream().anyMatch(
                    file ->
                            (file.getSize() < 5_242_880) &
                                    (file.getOriginalFilename().endsWith(".jpg") || file.getOriginalFilename().endsWith(".png") || file.getOriginalFilename().endsWith(".tiff"))
            );
        return false;
    }
}
