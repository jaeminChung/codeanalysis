package jio.codeanalysis;

import jio.codeanalysis.java.processor.TypeProcessor;
import jio.codeanalysis.util.ParserEnvironment;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FileASTRequestor;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class JavaAnalysis {
    static final Logger logger = Logger.getLogger(JavaAnalysis.class.getName());

    public static void main(String[] args) {
        JavaAnalysis ja = new JavaAnalysis();
        ja.parse(args);
    }

    public void parse(String[] sourceFilePath) {
        final Map<String, CompilationUnit> parsedCompilationUnits = new HashMap<>();

        ASTParser parser = ASTParser.newParser(AST.JLS9);

        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setResolveBindings(true);
        parser.setBindingsRecovery(true);
        parser.setEnvironment(ParserEnvironment.getClassPath()
                , new String[] {"/home/hijam/dev/jio.codeanalysis/src/test/resources/"}
                , ParserEnvironment.getEncoding()
                , true);
        parser.createASTs(sourceFilePath, ParserEnvironment.getEncoding(), new String[0]
                , new FileASTRequestor() {
                    @Override
                    public void acceptAST(String sourceFilePath, CompilationUnit ast) {
                        parsedCompilationUnits.put(sourceFilePath, ast);
                    }
                }, new NullProgressMonitor());
        /*
        parser.setSource( SourceFile.readFileToCharArray(sourceFilePath) );
        parser.setUnitName(sourceFilePath);
        final CompilationUnit cu = (CompilationUnit) parser.createAST(new NullProgressMonitor());
        cu.recordModifications();
        cu.accept( new TypeProcessor() );
        */
        for(CompilationUnit cu : parsedCompilationUnits.values()) {
            cu.recordModifications();
            cu.accept( new TypeProcessor() );
        }
    }

}

