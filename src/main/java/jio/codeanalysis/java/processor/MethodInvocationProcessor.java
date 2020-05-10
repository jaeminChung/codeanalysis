package jio.codeanalysis.java.processor;

import jio.codeanalysis.java.model.JavaMethodInvocation;
import jio.codeanalysis.java.model.JavaStatement;
import jio.codeanalysis.util.ASTUtil;
import org.eclipse.jdt.core.dom.*;

import java.util.List;
import java.util.Objects;
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
        addMethodInvocation(calleeMethod, node.arguments());
        return super.visit(node);
    }

    @Override
    public boolean visit(ClassInstanceCreation node) {
        IMethodBinding calleeMethod = node.resolveConstructorBinding();
        addMethodInvocation(calleeMethod, node.arguments());
        return super.visit(node);
    }

    public void addMethodInvocation(IMethodBinding calleeMethod, List args) {
        if (Objects.nonNull(calleeMethod)) {
            String caller = javaStatement.getJavaMethod().getQualifiedName();
            String callee = ASTUtil.getMethodQualifiedName(calleeMethod);

            JavaMethodInvocation invocation = new JavaMethodInvocation();
            invocation.setCallerQualifiedName(caller);
            invocation.setCalleeQualifiedName(callee);

            for (Object arg : args) {
                invocation.addInputValue(arg.toString());
            }
            javaStatement.addMethodInvocation(invocation);
        }
    }
}
