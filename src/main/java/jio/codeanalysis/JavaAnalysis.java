package jio.codeanalysis;

import jio.codeanalysis.java.processor.SourceScanner;
import jio.codeanalysis.java.processor.TypeProcessor;
import jio.codeanalysis.util.HibernateUtil;
import jio.codeanalysis.util.ParserEnvironment;
import jio.codeanalysis.util.SourceFile;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FileASTRequestor;
import org.hibernate.Session;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class JavaAnalysis {
    static final Logger logger = Logger.getLogger(JavaAnalysis.class.getName());

    public static void main(String[] args) {
        JavaAnalysis ja = new JavaAnalysis();
        ja.parse(args, "");
    }

    public void parse(String[] sourceFilePath, String projectPath) {
        final Map<String, CompilationUnit> parsedCompilationUnits = new HashMap<>();

        ASTParser parser = getParser(projectPath);
        parser.createASTs(sourceFilePath, ParserEnvironment.getEncoding(), new String[0]
                , new FileASTRequestor() {
                    @Override
                    public void acceptAST(String sourceFilePath, CompilationUnit ast) {
                        parsedCompilationUnits.put(sourceFilePath, ast);
                    }
                }, new NullProgressMonitor());

        SourceScanner scanner = new SourceScanner();
        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        
        for(String filePath : parsedCompilationUnits.keySet()) {
            char[] sourceCode = SourceFile.readFileToCharArray(filePath);
            scanner.setSource(sourceCode);

            CompilationUnit cu = parsedCompilationUnits.get(filePath);
            cu.recordModifications();
            cu.accept( new TypeProcessor( session, scanner) );
        }

        session.getTransaction().commit();
        session.close();
    }

    private ASTParser getParser(String projectPath) {
        ASTParser parser = ASTParser.newParser(AST.JLS9);

        Map options = JavaCore.getOptions();
        parser.setCompilerOptions(options);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setResolveBindings(true);
        parser.setBindingsRecovery(true);
        parser.setEnvironment(ParserEnvironment.getClassPath()
                , new String[] {projectPath}
                , ParserEnvironment.getEncoding()
                , true);

        return parser;
    }
}

