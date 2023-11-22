package org.narcissus.services.OCR;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

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

public final class PythonMapper {

    private final ProcessBuilder pb;
    private final static String linuxUserName = System.getenv("USER");
    private final static String targetDirectory = "/home/user/ITE/Excels";
    Logger logger = LoggerFactory.getLogger(PythonMapper.class);

    public PythonMapper(String sourceDir, String identifier) {
        pb = new ProcessBuilder(
                "python3",
                "/home/" + linuxUserName + "/ITE/python_mapper.py",
                sourceDir,        //arg[1] 'source'
                targetDirectory, //arg[2] 'target'
                identifier);    //arg[3] 'controller identifier'
        pb.redirectErrorStream(true);
        pb.redirectOutput(new File("/home/user/output.txt"));
        try {
            pb.start();
        } catch (IOException e) {
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
