import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

public class Python {
    private final static String targetDirectory = "/home/user/ITE/Excels";
    @Test
    public void givenPythonScript_whenPythonProcessInvoked_thenSuccess() throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder(
                "python3",
                "/home/user/ITE/python_mapper.py","/home/user/ITE/Tesseract/ITEe0590ac",
                targetDirectory,
                "ITEe0590ac");
        processBuilder.redirectErrorStream(true);
        processBuilder.redirectOutput(new File("/home/user/output.txt"));

        Process process = processBuilder.start();
//        String results = process.getInputStream().readAllBytes().toString();
//        System.out.println(results);
//        assertThat("Results should not be empty", results, is(not(empty())));
//        assertThat("Results should contain output of script: ", results, hasItem(
//                containsString("Hello Baeldung Readers!!")));
//
//        int exitCode = process.waitFor();
//        assertEquals("No errors should be detected", 0, exitCode);
    }
}
