package jio.codeanalysis.util;

import java.io.File;
import java.util.logging.Logger;

public class ParserEnvironment {
    private static final Logger logger = Logger.getLogger(ParserEnvironment.class.getName());

    private static final String UTF_8 = "UTF-8";
    private static final String SJIS = "SJIS";
    private static final String EUC_JP = "EUC-JP";
    private static final String EUC_KR = "EUC-KR";
    private static final String MS949 = "MS949";

    public static String[] getClassPath() {
        String property = System.getProperty("java.class.path", ".");
        String[] classPath = property.split(File.pathSeparator);
        logger.info(String.format("Class path : %s", property));
        return property.split(File.pathSeparator);
    }

    public static String[] getSourcePath() {
        return new String[] { "." };
    }

    public static String[] getEncoding() {
        return new String[] {UTF_8};
    }

    public static String getLineSeparator() {
        return System.getProperty("line.separator", "\n");
    }
}
