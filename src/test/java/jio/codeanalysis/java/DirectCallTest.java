package jio.codeanalysis.java;

import org.junit.Test;
import java.net.URL;

import jio.codeanalysis.JavaAnalysis;

public class DirectCallTest {
    @Test
    public void directCall() {
        JavaAnalysis ja = new JavaAnalysis();

        URL filePath = DirectCallTest.class.getResource("/DirectCall.java");
        URL projectPath = DirectCallTest.class.getResource("/");
        String[] sourceFilePath = {filePath.getPath()};

        ja.parse(sourceFilePath, projectPath.getPath());
    }
}
