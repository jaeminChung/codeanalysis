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
    private Scanner scanner;

    public SourceScanner() {
        scanner = new Scanner(true, false, false
                , ClassFileConstants.JDK1_8, null, null, true);
    }

    public void setSource(char[] source) {
        //tokenScanner = new TokenScanner(scanner);
        //        tokenScanner.getScanner().setSource(source);
    }
    public String getComment(ASTNode node) {
        int nodeStart = node.getStartPosition();
        tokenScanner.setOffset(nodeStart);

        try {
            int lastPos = tokenScanner.getPreviousTokenEndOffset(tokenScanner.readNext(false), nodeStart);
            int commentStart = getTokenCommentStart(lastPos, nodeStart);
            tokenScanner.setOffset(commentStart);

            return new String(tokenScanner.getScanner().getCurrentTokenSource());
        } catch (CoreException e) {
            logger.severe(e.getMessage());
        }
        return null;
    }
    /**
	 * Evaluates the start offset of comments directly ahead of a token specified by its start offset
	 * 
	 * @param lastPos An offset to before the node start offset. Can be 0 but better is the end location of the previous node. 
	 * @param nodeStart Start offset of the node to find the comments for.
	 * @return Returns the start offset of comments directly ahead of a token.
	 * @exception CoreException Thrown when a lexical error was detected while scanning (code LEXICAL_ERROR)
	 */		
	public int getTokenCommentStart(int lastPos, int nodeStart) throws CoreException {
		tokenScanner.setOffset(lastPos);

		int res= -1;
        /*
        int prevEndPos= lastPos;
		int prevEndLine= prevEndPos > 0 ? tokenScanner.getLineOfOffset(prevEndPos - 1) : 0;
		int nodeLine= tokenScanner.getLineOfOffset(nodeStart);

		int curr= tokenScanner.readNextWithEOF(false);
		int currStartPos= tokenScanner.getCurrentStartOffset();
		int currStartLine= tokenScanner.getLineOfOffset(currStartPos);
		while (curr != ITerminalSymbols.TokenNameEOF && nodeStart > currStartPos) {
			if (TokenScanner.isComment(curr)) {
				int linesDifference= currStartLine - prevEndLine;
				if ((linesDifference > 1) || (res == -1 && (linesDifference != 0 || nodeLine == currStartLine))) {
					res= currStartPos; // begin new
				}
			} else {
				res= -1;
			}
			
			if (curr == ITerminalSymbols.TokenNameCOMMENT_LINE) {
				prevEndLine= currStartLine;
			} else {
				prevEndLine= tokenScanner.getLineOfOffset(tokenScanner.getCurrentEndOffset() - 1);
			}					
			curr= tokenScanner.readNextWithEOF(false);
			currStartPos= tokenScanner.getCurrentStartOffset();
			currStartLine= tokenScanner.getLineOfOffset(currStartPos);
		}
		if (res == -1 || curr == ITerminalSymbols.TokenNameEOF) {
			return nodeStart;
		}
		if (currStartLine - prevEndLine > 1) {
			return nodeStart;
		}			
        */
		return res;
	}
}
