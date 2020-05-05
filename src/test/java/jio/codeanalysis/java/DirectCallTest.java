package jio.codeanalysis.java;

import java.net.URL;

import org.junit.Test;

import jio.codeanalysis.JavaAnalysis;

public class DirectCallTest {
    @Test
    public void directCall() {
        JavaAnalysis ja = new JavaAnalysis();
        URL c = DirectCallTest.class.getResource("./");
//        URL filePath = DirectCallTest.class.getResource("./DirectCall.java");
//        URL projectPath = DirectCallTest.class.getResource("./");
        URL filePath1 = DirectCallTest.class.getResource("/java/CallInterface.java");
        URL filePath2 = DirectCallTest.class.getResource("/java/DirectCall.java");
        URL filePath3 = DirectCallTest.class.getResource("/java/DirectCallee.java");
        URL projectPath = DirectCallTest.class.getResource("/java/");
        String[] sourceFilePath = {filePath1.getPath(), filePath2.getPath(), filePath3.getPath()};

        ja.parse(projectPath.getPath(), sourceFilePath);
    }
}
