package org.narcissus.services.python;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Tesseract stands for <b>Optical Character Recognition</b>.<br>
 * <p> New Tesseract instance is created for each web request. <br>
 * <b>"A1 = 'text'| B1 = picture.png" <br>
 * "A2 = 'text'| B2 = picture1.png" <br>
 * and so on ... </b><br>
 * in one .xlsx file </p>
 *
 * @since 1.0
 */

public final class PythonMapper implements Runnable {

    private final static Logger logger = LoggerFactory.getLogger(PythonMapper.class);
    private final String sourceDir;
    private final String targetDir;
    private final String identifier;

    private PythonMapper(String sourceDir, String identifier) {
        this.sourceDir = sourceDir;
        this.targetDir = sourceDir; //Yes we put excel into the same directory
        this.identifier = identifier;
    }

    public static PythonMapper of(String sourceDir, String identifier) {
        return new PythonMapper(sourceDir, identifier);
    }

    @Override
    public void run() {
        logger.info("Beginning processing of the directory with python script.");
        try {
            new ProcessBuilder(
                    "python3",
                    System.getenv("HOME") + "/ITE/python_mapper.py",
                    sourceDir,        //arg[1] 'source'
                    targetDir,       //arg[2] 'target'
                    identifier      //arg[3] 'controller identifier'
            ).start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
