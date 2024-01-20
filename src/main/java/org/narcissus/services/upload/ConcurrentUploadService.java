package org.narcissus.services.upload;

import org.narcissus.DTO.RequestDTO;
import org.narcissus.exceptions.ITEUploadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Instance of this class takes control when a new web request is made.
 * <p>Files that come from the request are saved to (Linux file system only):</p>
 * <em><b>'/home/ITE/uploads'</b></em> <br>
 * From there they are passed to Tesseract.
 *
 * @since 1.0
 */
@Service
public class ConcurrentUploadService implements UploadService {

    private static final String ITE_UPLOAD_DIRECTORY = System.getenv("HOME") + "/ITE/uploads";
    Logger logger = LoggerFactory.getLogger(ConcurrentUploadService.class);

    @Override
    public Optional<String> uploadFiles(RequestDTO dto) {
        String uploadDirectoryName = createUploadDirectory(dto.uuid());

        CountDownLatch doneSignal = new CountDownLatch(dto.size());
        ExecutorService service = Executors.newFixedThreadPool(dto.size());

        dto.files().forEach(file -> service.execute(new WriterWorker(doneSignal, file, uploadDirectoryName)));
        logger.info("Successfully written files to the disk");
        try {
            doneSignal.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        return Optional.of(uploadDirectoryName);
    }

    private String createUploadDirectory(String dtoId) {
        String directoryPath = new StringBuilder().append(ITE_UPLOAD_DIRECTORY).append("/ite").append(dtoId).toString();
        if (new File(directoryPath).mkdir()) {
            logger.debug("Created new upload directory " + directoryPath + ".");
            return directoryPath;
        }
        throw new ITEUploadException("Failed to create directory", new RuntimeException());
    }

    private class WriterWorker implements Runnable {
        private final CountDownLatch doneSignal;
        private final MultipartFile file;
        private final String target;

        WriterWorker(CountDownLatch doneSignal, MultipartFile file, String target) {
            this.doneSignal = doneSignal;
            this.file = file;
            this.target = target;
        }

        @Override
        public void run() {
            try {
                Path path = Paths.get(target, file.getOriginalFilename());
                Files.write(path, file.getBytes(), StandardOpenOption.CREATE);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            doneSignal.countDown();
        }
    }

}
