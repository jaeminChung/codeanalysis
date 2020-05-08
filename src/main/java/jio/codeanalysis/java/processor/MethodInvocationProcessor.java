package jio.codeanalysis.java.processor;

import jio.codeanalysis.java.model.JavaMethod;
import jio.codeanalysis.java.model.JavaMethodInvocation;
import jio.codeanalysis.java.model.JavaParameter;
import jio.codeanalysis.java.model.JavaStatement;
import jio.codeanalysis.util.ASTUtil;
import org.eclipse.jdt.core.dom.*;

import javax.persistence.EntityManager;
import java.util.logging.Logger;

public class MethodInvocationProcessor extends ASTVisitor{
    final static Logger logger = Logger.getLogger(MethodInvocationProcessor.class.getName());
    final JavaStatement javaStatement;

    public MethodInvocationProcessor(JavaStatement statement) {
        this.javaStatement = statement;
    }

    @Override
    public boolean visit(MethodInvocation node) {
        IMethodBinding calleeMethod = node.resolveMethodBinding();
        if (calleeMethod != null) {
            ASTNode parent = node.getParent();
            while (!(parent instanceof MethodDeclaration)) {
                parent = parent.getParent();
            }
            String caller = javaStatement.getJavaMethod().getQualifiedName();
            String callee = ASTUtil.getMethodQualifiedName(calleeMethod);

            JavaMethodInvocation invocation = new JavaMethodInvocation();
            invocation.setCallerQualifiedName(caller);
            invocation.setCalleeQualifiedName(callee);

            for (Object arg : node.arguments()) {
                invocation.addInputValue(arg.toString());
            }
            javaStatement.addMethodInvocation(invocation);

        }

        return super.visit(node);
    }
}
