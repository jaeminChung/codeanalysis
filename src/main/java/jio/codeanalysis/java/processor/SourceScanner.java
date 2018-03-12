package jio.codeanalysis.java.processor;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.parser.Scanner;
import org.eclipse.jdt.internal.core.dom.rewrite.TokenScanner;

import java.util.logging.Logger;

public class SourceScanner {
    private final static Logger logger = Logger.getLogger(SourceScanner.class.getName());
    private TokenScanner tokenScanner;

    public SourceScanner(char[] source) {
        Scanner scanner = new Scanner(true, false, false
                , ClassFileConstants.JDK1_8, null, null, true);
        scanner.setSource(source);
        tokenScanner = new TokenScanner(scanner);
    }

    public void setSource(char[] source) {
        tokenScanner.getScanner().setSource(source);
    }
    public String getPreviousComment(ASTNode node) {
        int start = node.getStartPosition();
        tokenScanner.setOffset(start-1);
        try {
            int token = tokenScanner.readNext(false);
            int prevToken = tokenScanner.getPreviousTokenEndOffset(token, 0);
            tokenScanner.readToToken(prevToken, 0);
            return new String(tokenScanner.getScanner().getCurrentTokenSource());
        } catch (CoreException e) {
            logger.severe(e.getMessage());
        }
        return null;
    }
}
