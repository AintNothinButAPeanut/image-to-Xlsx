package org.narcissus.services.OCR;

import java.io.File;
import java.util.Arrays;

public final class ITEProcessor {

    private final String sourceDirectory;
    private final String identifier;

    public ITEProcessor(String sourceDirectory, String identifier) {
        this.sourceDirectory = sourceDirectory;
        this.identifier = identifier;
        runOCR(sourceDirectory);
        runPython();
    }

    public void runOCR(String sourceDirectory) {
        Arrays.asList(
                        new File(sourceDirectory).listFiles())
                .forEach(file -> new OCR(file.getAbsolutePath()));
    }

    public void runPython() {
        new PythonMapper(sourceDirectory, identifier);
    }
}
