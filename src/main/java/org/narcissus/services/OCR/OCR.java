package org.narcissus.services.OCR;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.leptonica.PIX;
import org.bytedeco.tesseract.TessBaseAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.bytedeco.leptonica.global.leptonica.pixDestroy;
import static org.bytedeco.leptonica.global.leptonica.pixRead;

public final class OCR {

    Logger logger = LoggerFactory.getLogger(OCR.class);
    private final static String linuxHome = System.getenv("HOME");
    private static final String iteHomeDir = linuxHome + "/ITE";
    private final TessBaseAPI api = new TessBaseAPI();
    private String picturePath;
    private String txtPath;
    private BytePointer outText;

    public OCR(String picturePath) {
        logger.info("Beginning processing pictures with Tesseract.");
        this.picturePath = picturePath;
        this.txtPath = picturePath.substring(0, picturePath.lastIndexOf('.')) + ".txt";
        run();
    }

    private void run() {
        if (api.Init(iteHomeDir, "rus") != 0) {
            System.err.println("Could not initialize tesseract.");
            System.exit(1);
        }

        PIX image = pixRead(picturePath);
        api.SetImage(image);

        outText = api.GetUTF8Text();

        try {
            Files.createFile(
                    Paths.get(txtPath)
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try (FileOutputStream fileOutputStream = new FileOutputStream(txtPath)) {
            fileOutputStream.write(outText.getStringBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        outText.deallocate();
        api.End();

        pixDestroy(image);
    }
}
