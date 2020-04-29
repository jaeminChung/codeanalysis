package jio.codeanalysis.java.processor;

import java.util.logging.Logger;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

public class MethodProcessor extends ASTVisitor{
    final static Logger logger = Logger.getLogger(MethodProcessor.class.getName());

    @Override
    public boolean visit(VariableDeclarationStatement node) {
        String code = node.toString();
        return false;
    }
}
