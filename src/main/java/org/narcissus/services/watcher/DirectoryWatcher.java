package org.narcissus.services.watcher;

import org.narcissus.exceptions.ITEDirectoryWatcherException;
import org.narcissus.services.OCR.ITEProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;

@Component
public class DirectoryWatcher implements CommandLineRunner {

    Logger logger = LoggerFactory.getLogger(DirectoryWatcher.class);

    @Value("${ite.uploadsDir}")
    private String iteUploadDir;
    @Value("${ite.OCRDir}")
    private String iteOCRDir;
    @Value("${ite.excelsDir}")
    private String iteExcelDir;
    private WatchService watchService;
    private WatchKey OCRDirKey, excelKey;

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
            OCRDirKey = registerNewDirectoryWatcher(iteOCRDir, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE);
            excelKey = registerNewDirectoryWatcher(iteExcelDir, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE);
            List<WatchKey> keys = List.of(OCRDirKey, excelKey);
            logger.info("WatchService is up and running.");

            for (WatchKey key : keys) {
                while ((key = watchService.take()) != null) {
                    //512 events at a time at max
                    key.pollEvents().forEach(this::manageWatcherEvent);
                    key.reset();
                }
            }

            watchService.close();
        } catch (IOException | InterruptedException exception) {
            throw new ITEDirectoryWatcherException("Failed to register a new WatchKey or to close a watchService.", exception);
        }
    }

    private void manageWatcherEvent(WatchEvent<?> event) {
        //Proper observer pattern https://proglib.io/p/monitoring-faylov-vmeste-s-java-nio-2020-01-25
        switch (event.kind().toString()) {
            case "ENTRY_CREATE" -> {
                logger.info("Detected new file");
                if (event.context().toString().contains("ITE") & !event.context().toString().contains("xlsx")) {
                    process(event);
                } else {
                    //TODO: scheduled cleanup at night
                    //cleanUp();
                }
            }

            case "ENTRY_DELETE" -> {
            }

            case "ENTRY_MODIFY" -> {
            }

            case "OVERFLOW" -> {
            }
            //For some reason overflow even is always triggered. Do not thor exceptions here otherwise crashes are inevitable
        }
    }

    private WatchKey registerNewDirectoryWatcher(String directoryPath, WatchEvent.Kind<?>... kinds) {
        try {
            return Paths.get(directoryPath).register(watchService, kinds);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //TODO: do it on a schedule
    private void cleanUp(){
        for(File directory : new File(iteOCRDir).listFiles()) {
            for (File file: directory.listFiles()) {
                file.delete();
            }
            directory.delete();
        }
    }

    private void process(WatchEvent<?> event){
        String identifier = event.context().toString();
        new ITEProcessor(iteOCRDir + "/" + event.context(), identifier);
    }

}
