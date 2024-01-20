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

public final class PythonMapper {

    private final static ProcessBuilder pb = new ProcessBuilder();
    private final static String linuxHome = System.getenv("HOME");
    private final static String targetDirectory = linuxHome + "/ITE/excels";
    private final static Logger logger = LoggerFactory.getLogger(PythonMapper.class);

    public static synchronized void mapToExcel(String sourceDir, String identifier) {
        logger.info("Beginning processing of the directory with python script.");
        pb.command(
                "python3",
                linuxHome + "/ITE/python_mapper.py",
                sourceDir,        //arg[1] 'source'
                targetDirectory, //arg[2] 'target'
                identifier      //arg[3] 'controller identifier'
        );
        try {
            pb.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
