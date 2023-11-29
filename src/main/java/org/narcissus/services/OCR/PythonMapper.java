package org.narcissus.services.OCR;

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

public final class PythonMapper {

    private final ProcessBuilder pb;
    private final static String linuxHome = System.getenv("HOME");
    private final static String targetDirectory = linuxHome + "/ITE/excels";
    Logger logger = LoggerFactory.getLogger(PythonMapper.class);

    public PythonMapper(String sourceDir, String identifier) {
        logger.info("Beginning processing of the directory with python script.");
        pb = new ProcessBuilder(
                "python3",
                linuxHome + "/ITE/python_mapper.py",
                sourceDir,        //arg[1] 'source'
                targetDirectory, //arg[2] 'target'
                identifier);    //arg[3] 'controller identifier'
        try {
            pb.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
