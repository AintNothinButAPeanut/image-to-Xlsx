package org.narcissus.services.OCR;

import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.leptonica.PIX;
import org.bytedeco.tesseract.TessBaseAPI;
import org.narcissus.exceptions.ITETesseractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.bytedeco.leptonica.global.leptonica.pixDestroy;
import static org.bytedeco.leptonica.global.leptonica.pixRead;

@Service
public final class Tesseract implements OpticalCharacterRecognition {

    Logger logger = LoggerFactory.getLogger(Tesseract.class);

    @Override
    public void scanText(File directoryWithPictures) {
        int filesCount = directoryWithPictures.listFiles().length;
        CountDownLatch doneSignal = new CountDownLatch(filesCount);
        ExecutorService service = Executors.newFixedThreadPool(filesCount);
        Arrays.stream(directoryWithPictures.listFiles()).forEach(picture -> service.execute(new TesseractWorker(doneSignal, picture)));
        try {
            doneSignal.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private class TesseractWorker implements Runnable {

        private final TessBaseAPI api = new TessBaseAPI();
        private final CountDownLatch doneSignal;
        private final File picture;
        private final String txtPath;
        private BytePointer outText;

        TesseractWorker(CountDownLatch doneSignal, File picture) {
            this.doneSignal = doneSignal;
            this.picture = picture;
            this.txtPath = picture.getParent() + "/" + picture.getName().substring(0, picture.getName().lastIndexOf('.')) + ".txt";
        }

        @Override
        public void run() {
            if (api.Init(System.getenv("HOME") + "/ITE", "rus+eng") != 0) {
                throw new ITETesseractException("Failed to start Tesseract process", new RuntimeException());
            }
            PIX image = pixRead(picture.getAbsolutePath());
            api.SetImage(image);
            outText = api.GetUTF8Text();

            try {
                Files.createFile(Paths.get(txtPath));
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

            doneSignal.countDown();
        }
    }
}
