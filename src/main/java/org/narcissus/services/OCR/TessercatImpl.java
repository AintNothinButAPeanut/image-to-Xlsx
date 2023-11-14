package org.narcissus.services.OCR;

import org.narcissus.exceptions.ITETesseractException;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * This class is intended to build the TesseractOCR command. <Br>
 * The result, later passed into {@link java.lang.Object#toString()} ,  will look similar to this, depending on passed arguments:
 * </p>
 *
 * <em>{@code 'tesseract --tessdata-dir "/usr/share" "/home/imagename.png" "/home/outputname" -l rus'} </em><br>
 *
 * <p>
 * Note that "/home/outputname" will be automatically created as a ".txt" file so there is no need for explicit creation of a ".txt" file.
 * </p>
 *
 * @since 1.0
 */
@Deprecated
public class TessercatImpl extends Thread {

    private final String tesseractCommand;
    private final ProcessBuilder pb;
    @Value("${ite.tessdataDir}")
    private String ITE_TESSDATA_DIR;

    public TessercatImpl(String inputPath, String outputPath) {
        this.pb = new ProcessBuilder();
        this.setName("TesseractCommand@" + this.hashCode());
        this.setDaemon(false);
        this.tesseractCommand = String.format("tesseract --tessdata-dir %s %s %s -l rus", ITE_TESSDATA_DIR, inputPath, outputPath);
        this.start();
    }

    @Override
    public void run() {
        pb.command("/bin/sh", "-c", tesseractCommand);
        try {
            pb.start().waitFor(20, TimeUnit.SECONDS);
        } catch (IOException | InterruptedException exception) {
            throw new ITETesseractException("Either thread timed out (20 seconds) or a new thread failed to start.", exception);
        }
    }

    @Override
    public String toString() {
        return tesseractCommand;
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
