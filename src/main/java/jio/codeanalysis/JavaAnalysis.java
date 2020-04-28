package jio.codeanalysis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.Comment;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FileASTRequestor;
import org.hibernate.Session;

import jio.codeanalysis.java.processor.CommentProcessor;
import jio.codeanalysis.java.processor.TypeProcessor;
import jio.codeanalysis.util.HibernateUtil;
import jio.codeanalysis.util.ParserEnvironment;
import jio.codeanalysis.util.SourceFile;

public class JavaAnalysis {
    static final Logger logger = Logger.getLogger(JavaAnalysis.class.getName());

    public static void main(String... args) {
        JavaAnalysis ja = new JavaAnalysis();
        ja.parse("", args);
    }

    private boolean isConditionCall() {
    	return true;
    }
    public void parse(String projectPath, String... sourceFilePaths) {
        final Map<String, CompilationUnit> parsedCompilationUnits = new HashMap<>();

        if(isConditionCall()) {
        	
        ASTParser parser = getParser(projectPath);
        parser.createASTs(sourceFilePaths, ParserEnvironment.getEncoding(), new String[0]
                , new FileASTRequestor() {
                    @Override
                    public void acceptAST(String sourceFilePath, CompilationUnit ast) {
                        parsedCompilationUnits.put(sourceFilePath, ast);
                    }
                }, new NullProgressMonitor());

        Session session = HibernateUtil.getSessionFactory().openSession();
        session.beginTransaction();
        for(String filePath : parsedCompilationUnits.keySet()) {

            CompilationUnit cu = parsedCompilationUnits.get(filePath);
            cu.recordModifications();
            cu.accept( new TypeProcessor( session ) );
            
            char[] sourceCode = SourceFile.readFileToCharArray(filePath);
            @SuppressWarnings("unchecked")
			List<Comment> comments = (List<Comment>) cu.getCommentList();
            for(Comment comment: comments) {
            	comment.accept(new CommentProcessor(cu, sourceCode));
            }
        }

        session.getTransaction().commit();
        session.close();
        
        
        }
    }

    private ASTParser getParser(String projectPath) {
        ASTParser parser = ASTParser.newParser(AST.JLS8);

        Map<String, String> options = JavaCore.getOptions();
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

