package jio.codeanalysis.java.processor;

import jio.codeanalysis.java.model.CallRelation;
import jio.codeanalysis.java.model.JavaMethod;
import jio.codeanalysis.java.model.JavaParameter;
import org.eclipse.jdt.core.dom.*;

import javax.persistence.EntityManager;
import java.util.StringJoiner;
import java.util.logging.Logger;

public class MethodProcessor extends ASTVisitor{
    final static Logger logger = Logger.getLogger(MethodProcessor.class.getName());
    private final EntityManager em;
    private final String filePath;

    public MethodProcessor(EntityManager em, String filePath) {
        this.em = em;
        this.filePath = filePath;
    }

    @Override
    public boolean visit(MethodDeclaration node) {
        IMethodBinding method = node.resolveBinding();

        if (method != null) {
            JavaMethod javaMethod = new JavaMethod();
            javaMethod.setMethodName(method.getName());
            javaMethod.setQualifiedName(getMethodQualifiedName(method));

            // modifiers
            javaMethod.setModifiers(method);

            // input parameter
            //saveInputParameter(node, javaMethod);

            //output parameter
            //saveReturnParameter(method, javaMethod);

            node.getBody().accept(new StatementProcessor(javaMethod));
            em.persist(javaMethod);
        }

        return super.visit(node);
    }

    private String getMethodQualifiedName(IMethodBinding method) {
        String typeQualifiedName = method.getDeclaringClass().getQualifiedName();
        String methodName = method.getName() + getParameters(method);

        return String.format("%s.%s", typeQualifiedName, methodName);
    }

    private String getMethodQualifiedName(MethodDeclaration node) {
        String qualifiedName = "";

        IMethodBinding method = node.resolveBinding();
        if (method != null) {
            qualifiedName = getMethodQualifiedName(method);
        }

        return qualifiedName;
    }

    private String getParameters(IMethodBinding method) {

        StringJoiner joiner = new StringJoiner(",");
        for (ITypeBinding tb : method.getParameterTypes()) {
            joiner.add(tb.getName());
        }
        return String.format("(%s)", joiner.toString());
    }

    private void saveInputParameter(MethodDeclaration node, JavaMethod javaMethod) {
        int seq = 0;
        JavaParameter param;
        for (Object o : node.parameters()) {
            if (o instanceof VariableDeclaration) {
                VariableDeclaration var = (VariableDeclaration) o;

                param = new JavaParameter();
                param.setInput(true);
                param.setMethodQualifiedName(javaMethod.getQualifiedName());
                param.setParameterName(var.getName().getIdentifier());
                param.setTypeQualifiedName(var.resolveBinding().getType().getQualifiedName());
                param.setParamSeq(seq);
                if (var instanceof SingleVariableDeclaration) {
                    SingleVariableDeclaration svd = (SingleVariableDeclaration) var;
                    Type paramType = svd.getType();
                    if (paramType.isParameterizedType()) {
                        ParameterizedType pt = (ParameterizedType) paramType;
                    }
                    param.setArray(paramType.isArrayType());
                }
            }
            seq++;
        }
    }

    private void saveReturnParameter(IMethodBinding method, JavaMethod javaMethod) {
        JavaParameter param = new JavaParameter();
        param.setInput(false);
        param.setMethodQualifiedName(javaMethod.getQualifiedName());
        param.setParameterName(javaMethod.getMethodName());
        param.setTypeQualifiedName(method.getReturnType().getQualifiedName());
        param.setParamSeq(-1);

        em.persist(param);
    }

    @Override
    public boolean visit(MethodInvocation node) {
        IMethodBinding method = node.resolveMethodBinding();
        for (Object o : node.arguments()) {
            logger.info(String.format("call arguments : %s", o.toString()));
            if (o instanceof SimpleName) {
                SimpleName simpleName = (SimpleName) o;
                IBinding vb = simpleName.resolveBinding();
                if (vb != null) {
                    //searchVariable(vb.getJavaElement(), vb.getJavaElement());
                }
            }
        }
        if (method != null) {
            ASTNode parent = node.getParent();
            while ((parent instanceof MethodDeclaration) == false) {
                parent = parent.getParent();
            }
            String caller = getMethodQualifiedName((MethodDeclaration) parent);
            String callee = getMethodQualifiedName(method);

            CallRelation callRelation = new CallRelation();
            callRelation.setCaller(caller);
            callRelation.setCallee(callee);

            em.persist(callRelation);
        }

        return super.visit(node);
    }
}
