package jio.codeanalysis.java;

import org.junit.Test;
import java.net.URL;

import jio.codeanalysis.JavaAnalysis;

public class DirectCallTest {
    @Test
    public void directCall() {
        JavaAnalysis ja = new JavaAnalysis();

        //        URL rootPath = DirectCallTest.class.getResource("/");
        //System.out.println(rootPath.getPath());
        URL filePath = DirectCallTest.class.getResource("../../../src/test/resources/DirectCall.java");
        URL projectPath = DirectCallTest.class.getResource("../../../src/test/resources");
        String[] sourceFilePath = {filePath.getPath()};

        ja.parse(sourceFilePath, projectPath.getPath());
    }
}
