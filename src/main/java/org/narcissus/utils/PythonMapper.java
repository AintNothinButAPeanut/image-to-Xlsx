package org.narcissus.utils;

import org.narcissus.services.OCR.OCR;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * OCR stands for <b>Optical Character Recognition</b>.<br>
 * <p> New OCR instance, which is a new thread, is created for each web request. <br>
 * Those OCR instances create even more threads for each picture in the request to boost the speed with which each request is processed.</p>
 * <p>Python script then puts processed pictures and text in pairs relative to each other as follows <br>
 * <b>"A1 = 'text'| B1 = picture.png" <br>
 * "A2 = 'text'| B2 = picture1.png" <br>
 * and so on ... </b><br>
 * in one .xlsx file </p>
 *
 * @since 1.0
 */

public class PythonMapper extends Thread {

    private final File[] pictures;
    private final ProcessBuilder pb;
    private final File directoryWithPictures;
    Logger logger = LoggerFactory.getLogger(PythonMapper.class);
    @Value("${ite.processedDir}")
    private String iteProcessedDir;

    public PythonMapper(String dirToBeProcessed) {
        pb = new ProcessBuilder();
        this.directoryWithPictures = new File(dirToBeProcessed);
        pb.directory(this.directoryWithPictures);
        this.pictures = directoryWithPictures.listFiles();
        this.setName("PythonThread@" + this.hashCode() + " --- " + "Started on " + new Date());
        this.setDaemon(false);
        logger.info("New thread of OCR created. " + this.getName());
        this.start();
    }

    @Override
    public void run() {
        runTesseract();
        runPython();
    }

    private void runTesseract() {
        Arrays.stream(pictures).forEach(picture -> new OCR(picture.getAbsolutePath()));
    }

    private void runPython() {
        pb.command("/bin/sh", "-c", String.format("python pythonmapper.py %s", pictures[0].getParent()));
        try {
            pb.start().waitFor(30, TimeUnit.SECONDS);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}
