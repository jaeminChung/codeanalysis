package jio.codeanalysis.java.processor;

import org.eclipse.jdt.core.dom.*;

import java.util.logging.Logger;

public class MethodProcessor extends ASTVisitor{
    final static Logger logger = Logger.getLogger(MethodProcessor.class.getName());

    @Override
    public boolean visit(MethodDeclaration node) {
        IMethodBinding method = node.resolveBinding();
        if( method != null ) {
            logger.info(String.format("Method declaration : %s", node.toString()));
            logger.info(String.format("Method name : %s", method.getName()));

            // modifiers
            int modifiers = method.getModifiers();
            Modifier.isFinal(modifiers);
            Modifier.isStatic(modifiers);

            // input parameter
            int index = 0;
            for ( Object o : node.parameters() ) {
                if( o instanceof VariableDeclaration ) {
                    IVariableBinding var = ((VariableDeclaration) o).resolveBinding();
                    logger.info(String.format("  #%s Parameter Type : %s", index, var.getType().getQualifiedName()));
                    logger.info(String.format("  #%s Parameter Name : %s", index, var.getName()));
                }
                index++;
            }

            //output parameter
            logger.info(String.format("  Return Type : %s", method.getReturnType().getQualifiedName()));

            if( node.getBody() != null ) {
                StatementProcessor sp = new StatementProcessor();
                node.getBody().accept(sp);
            }
        }

        return false;
    }
}
