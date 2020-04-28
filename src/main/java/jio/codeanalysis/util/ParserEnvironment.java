package jio.codeanalysis.util;

import java.io.File;
import java.util.logging.Logger;

public class ParserEnvironment {
    private static final Logger logger = Logger.getLogger(ParserEnvironment.class.getName());

    public static String[] getClassPath() {
        String property = System.getProperty("java.class.path", ".");
        logger.info(String.format("Class path : %s", property));
        return property.split(File.pathSeparator);
    }

    public static String[] getSourcePath() {
        return new String[] { "." };
    }

    public static String[] getEncoding() {
        return new String[] {Encoding.UTF_8.getValue()};
    }

    public static String getLineSeparator() {
        return System.getProperty("line.separator", "\n");
    }
    
    public enum Encoding {
    	UTF_8("UTF-8"),
    	SJIS("SJIS"),
    	EUC_JP("EUC-JP"),
    	EUC_KR("EUC-KR"),
    	MS949("MS949");
    	
    	
    	String value;
    	
    	Encoding(String value) {
    		this.value = value;
    	}
    	
    	public String getValue() {
    		return value;
    	}
    }
}
