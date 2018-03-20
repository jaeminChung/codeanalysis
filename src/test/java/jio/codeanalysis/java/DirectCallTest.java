package jio.codeanalysis.java;

import jio.codeanalysis.JavaAnalysis;

import org.junit.Test;

public class DirectCallTest {
    @Test
    public void directCall() {
        JavaAnalysis ja = new JavaAnalysis();
        ///home/hijam/dev/jio.codeanalysis/src/test/resources
        String[] sourceFilePath = {"/root/dev/jio.codeanalysis/src/test/resources/DirectCall.java"};
        //sourceFilePath[1] = "/home/hijam/dev/jio.codeanalysis/src/test/resources/DirectCallee.java";
        ja.parse(sourceFilePath);
    }
}
