package org.narcissus.services.OCR;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

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

public final class PythonMapper extends Thread {

    private final static File workingDirectory = new File(System.getenv("HOME") + "/ITE");
    private final ProcessBuilder pb;
    private final File directoryWithPictures;
    private final static String targetDirectory = "/home/user/ITE/Excels";
    private final String identifier;
    Logger logger = LoggerFactory.getLogger(PythonMapper.class);

    public PythonMapper(String sourceDir, String identifier) {
        pb = new ProcessBuilder();
        pb.directory(workingDirectory);
        this.directoryWithPictures = new File(sourceDir);
        this.identifier = identifier;
        //this.setName("PythonThread@" + this.hashCode() + " --- " + "Started on " + new Date());
        this.setDaemon(true);
        this.start();
    }

    @Override
    public void run() {
        pb.command(
                "/bin/sh",
                "-c",
                String.format( //python3 python_mapper.py "/home/ITE/OCR/ITExxxx"  "/home/user/ITE/Excels"
                        "python3 python_mapper.py %s %s",
                        directoryWithPictures.getAbsolutePath(),
                        targetDirectory,
                        identifier));
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
