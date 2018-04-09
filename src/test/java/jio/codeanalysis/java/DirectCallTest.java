package jio.codeanalysis.java;

import jio.codeanalysis.JavaAnalysis;
import org.junit.Test;

import java.net.URL;

public class DirectCallTest {
    @Test
    public void directCall() {
        JavaAnalysis ja = new JavaAnalysis();

        URL filePath = DirectCallTest.class.getResource("/java/DirectCall.java");
        URL projectPath = DirectCallTest.class.getResource("/java/");
        String[] sourceFilePath = {filePath.getPath()};

        ja.parse(sourceFilePath, projectPath.getPath());
    }
}
