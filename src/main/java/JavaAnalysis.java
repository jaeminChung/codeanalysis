import jio.codeanalysis.java.processor.MethodProcessor;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;

public class JavaAnalysis {
    static final Logger logger = Logger.getLogger(JavaAnalysis.class.getName());

    public static void main(String[] args) {
        ASTParser parser = ASTParser.newParser(AST.JLS9);

        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setResolveBindings(true);
        parser.setBindingsRecovery(true);
        parser.setSource(readFileToCharArray(args[0]));

        String[] sources = { "" };
        String[] classpath = { "" };
        parser.setEnvironment(classpath, sources, new String[] {"UTF-8"}, true);
        parser.setUnitName("Test");

        final CompilationUnit cu = (CompilationUnit) parser.createAST(null);

        cu.accept( new MethodProcessor() );
    }

    public static char[] readFileToCharArray(String filePath) {
        StringBuilder fileData = new StringBuilder(1000);

        try {
            BufferedReader reader = new BufferedReader( new FileReader(filePath) );

            char[] buf = new char[10];
            int numRead = 0;

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

