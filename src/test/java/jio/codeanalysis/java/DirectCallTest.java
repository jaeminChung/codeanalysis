package jio.codeanalysis.java;

import java.net.URL;

import org.junit.Test;

import jio.codeanalysis.JavaAnalysis;

public class DirectCallTest {
    @Test
    public void directCall() {
        JavaAnalysis ja = new JavaAnalysis();
        URL c = DirectCallTest.class.getResource("./");
        URL filePath = DirectCallTest.class.getResource("./DirectCall.java");//("/java/DirectCall.java");
        URL projectPath = DirectCallTest.class.getResource("./"); //("/java/");
        String[] sourceFilePath = {filePath.getPath()};

        ja.parse(projectPath.getPath(), sourceFilePath);
    }
}
