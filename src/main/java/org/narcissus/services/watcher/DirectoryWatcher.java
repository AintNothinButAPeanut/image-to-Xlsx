package org.narcissus.services.watcher;

import org.narcissus.exceptions.ITEDirectoryWatcherException;
import org.narcissus.utils.PythonMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;

@Component
public class DirectoryWatcher implements CommandLineRunner {

    Logger logger = LoggerFactory.getLogger(DirectoryWatcher.class);
    @Value("${linux.home}")
    private String homeDir;
    @Value("${ite.OCRDir}")
    private String iteOcrDir;
    @Value("${ite.uploadsDir}")
    private String iteUploadDir;
    @Value("${ite.prefix}")
    private String itePrefix;
    private WatchService watchService;
    private WatchKey key;

    public DirectoryWatcher() {
        try {
            watchService = FileSystems.getDefault().newWatchService();
        } catch (IOException exception) {
            throw new ITEDirectoryWatcherException("Failed to instantiate WatchService field on SpringBean creation.", exception);
        }
    }

    @Override
    public void run(String... args) {
        try {
            key = registerNewDirectoryWatcher(iteOcrDir, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE);
            logger.info("WatchService is up and running.");

            while ((key = watchService.take()) != null) {
                //512 events at a time at max
                key.pollEvents().forEach(this::manageWatcherEvent);
                key.reset();
            }

            watchService.close();
        } catch (IOException | InterruptedException exception) {
            throw new ITEDirectoryWatcherException("Failed to register a new WatchKey or to close a watchService.", exception);
        }
    }

    private void manageWatcherEvent(WatchEvent<?> event) {
        //TODO THIS IS INCREDIBLY BAD. MUST adhere to this practices LATER https://proglib.io/p/monitoring-faylov-vmeste-s-java-nio-2020-01-25
        //TODO also use switch enhanced lambda way
        switch (event.kind().toString()) {
            case "ENTRY_CREATE":

                if (event.context().toString().contains("ITE")) {
                    logger.info(event.context() + " directory is MOVED");
                    new PythonMapper(iteOcrDir + "/" + event.context());
                }

            case "ENTRY_DELETE":
                //TODO implement logic
            case "ENTRY_MODIFY":
                //TODO implement logic
            case "OVERFLOW":
                throw new ITEDirectoryWatcherException("DirectoryWatcherOverflow happened.", new RuntimeException());
            default:

        }
    }

    private WatchKey registerNewDirectoryWatcher(String directoryPath, WatchEvent.Kind<?>... kinds) {
        try {
            return Paths.get(directoryPath).register(watchService, kinds);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
