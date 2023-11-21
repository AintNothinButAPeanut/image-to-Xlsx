package org.narcissus.services.local;

import org.narcissus.DTO.RequestDTO;
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
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.Date;

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
    @Value("${ite.prefix}")
    private String itePrefix;
    @Value("${ite.OCRDir}")
    private String iteOcrDir;
    private String uploadDirectory;

    public void uploadFiles(RequestDTO requestDTO) throws IOException {
        Collection<MultipartFile> files = requestDTO.files();

        if (!complyWithUploadRules(files)) {
            logger.info("Request made at " + new Date() + " does not meet the requirements");
            return;
        } else logger.info("Request met the requirements, proceeding.");

        uploadDirectory = createUploadDirectory(requestDTO.id());
        files.forEach(file -> writeFile(file, uploadDirectory));

        int filesWritten = new File(uploadDirectory).listFiles().length;

        if (requestDTO.size() != filesWritten) {
            throw new ITEUploadException("Some files were not written to the disk for unknown reason.\n"
                    + filesWritten + " files written out of " + requestDTO.size(), new RuntimeException());
        } else {
            logger.info("Successfully written to the disk " + filesWritten + " out of " + requestDTO.size() + "files.");
        }

        moveDirectory(Paths.get(uploadDirectory), Paths.get(iteOcrDir));
    }

    private void writeFile(MultipartFile file, String target) {
        try {
            Path path = Paths.get(target, file.getOriginalFilename()); // /home/user/ITE/uploads/ITExxxxx/filename.png
            Files.write(path, file.getBytes(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            throw new ITEUploadException("Failed to write files.", e);
        }
    }

    private String createUploadDirectory(String requestDTOId) {
        String path = iteUploadsDir + "/" + itePrefix + requestDTOId;
        logger.debug("Creating directory " + path + ".");
        if (new File(path).mkdir())
            return path;
        else throw new ITEUploadException("Failed to create directory", new RuntimeException());
    }

    //Do not forget that you move directories so that DirectoryWatcher gets a directory with 100% uploaded files and NOT AN EMPTY ONE. Duuuh
    private void moveDirectory(Path source, Path targetDir) {
        try {
            Path target = Paths.get(targetDir.toString(), source.getFileName().toString());
            Files.move(source, target);
        } catch (IOException e) {
            throw new ITEUploadException("Failed to move a directory with written files to a directory with WatcherService", e);
        }
    }

    private boolean complyWithUploadRules(Collection<MultipartFile> files) {
        //Each file must be under 5Mb size and of extension of .jpg .tiff or .png
        //If not return false
        if (files.size() <= 100)
            return files.stream().anyMatch(
                    file ->
                            (file.getSize() < 5_242_880) & //5_242_880 = 5Mb
                                    (file.getOriginalFilename().endsWith(".jpg") ||
                                            file.getOriginalFilename().endsWith(".png") ||
                                            file.getOriginalFilename().endsWith(".tiff"))
            );
        return false;
    }
}
