package jio.codeanalysis;

import jio.codeanalysis.java.processor.CommentProcessor;
import jio.codeanalysis.java.processor.TypeProcessor;
import jio.codeanalysis.util.HibernateUtil;
import jio.codeanalysis.util.ParserEnvironment;
import jio.codeanalysis.util.SourceFile;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.*;

import javax.persistence.EntityManager;
import java.net.URL;
import java.util.*;
import java.util.logging.Logger;

public class JavaAnalysis {
    static final Logger logger = Logger.getLogger(JavaAnalysis.class.getName());

    public static void main(String... args) {
        JavaAnalysis ja = new JavaAnalysis();
        URL current = JavaAnalysis.class.getResource(".");
        URL filePath = JavaAnalysis.class.getResource("./DirectCall.java");
        URL projectPath = JavaAnalysis.class.getResource(".");
        String[] sourceFilePath = {filePath.getPath()};

        ja.parse(projectPath.getPath(), sourceFilePath);
    }

    public void parse(String projectPath, String... sourceFilePaths) {
        final Map<String, CompilationUnit> parsedCompilationUnits = new HashMap<>();

        ASTParser parser = getParser(sourceFilePaths);
        parser.createASTs(sourceFilePaths
                , ParserEnvironment.getEncodings(sourceFilePaths.length)
                , new String[0]
                , new FileASTRequestor() {
                    @Override
                    public void acceptAST(String sourceFilePath, CompilationUnit ast) {
                        parsedCompilationUnits.put(sourceFilePath, ast);
                    }
                }, new NullProgressMonitor());

        EntityManager em = HibernateUtil.getEntityManager();
        em.getTransaction().begin();
        List<String> sortedKey = new ArrayList<>(parsedCompilationUnits.keySet());
        Collections.sort(sortedKey);
        for(String filePath : sortedKey) {

            CompilationUnit cu = parsedCompilationUnits.get(filePath);
            cu.accept( new TypeProcessor( em , filePath) );
            
            char[] sourceCode = SourceFile.readFileToCharArray(filePath);
            @SuppressWarnings("unchecked")
			List<Comment> comments = (List<Comment>) cu.getCommentList();
            for(Comment comment: comments) {
            	comment.accept(new CommentProcessor(cu, sourceCode));
            }
        }
        em.getTransaction().commit();
        em.close();
        HibernateUtil.closeEntityManagerFactory();
    }

    private ASTParser getParser(String... sourceFilePaths) {
        ASTParser parser = ASTParser.newParser(AST.JLS8);

        Map<String, String> options = JavaCore.getOptions();
        parser.setCompilerOptions(options);
        parser.setKind(ASTParser.K_COMPILATION_UNIT);
        parser.setResolveBindings(true);
        parser.setBindingsRecovery(true);
        parser.setStatementsRecovery(true);
        parser.setBindingsRecovery(true);
        parser.setEnvironment(ParserEnvironment.getClassPath()
                , new String[0]
                , new String[0]
                , true);

        return parser;
    }
}

