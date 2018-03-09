package jio.codeanalysis.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;

public class SourceFile {
    private final static Logger logger = Logger.getLogger(SourceFile.class.getName());

    public static char[] readFileToCharArray(String filePath) {
        StringBuilder fileData = new StringBuilder(1000);

        try {
            BufferedReader reader = new BufferedReader( new FileReader(filePath) );

            char[] buf = new char[10];
            int numRead;

            while( (numRead = reader.read(buf)) != -1 ) {
                String readData = String.valueOf(buf, 0, numRead);
                fileData.append(readData);
                buf = new char[1024];
            }

            reader.close();
        } catch( FileNotFoundException e ) {
            logger.severe("Can't find file.");
        } catch( IOException e ) {
            logger.severe("File reading error.");
        }

        return fileData.toString().toCharArray();
    }
}
