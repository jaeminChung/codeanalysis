package jio.codeanalysis.java.processor;

import jio.codeanalysis.java.model.JavaMethod;
import jio.codeanalysis.java.model.JavaParameter;
import jio.codeanalysis.util.ASTUtil;
import org.eclipse.jdt.core.dom.*;

import javax.persistence.EntityManager;
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
            javaMethod.setQualifiedName(ASTUtil.getMethodQualifiedName(method));

            // modifiers
            javaMethod.setModifiers(method);

            // input parameter
            saveInputParameter(node, javaMethod);

            //output parameter
            saveReturnParameter(method, javaMethod);

            node.getBody().accept(new StatementProcessor(javaMethod));
            em.persist(javaMethod);
        }

        return super.visit(node);
    }

    private void saveInputParameter(MethodDeclaration node, JavaMethod javaMethod) {
        int seq = 0;
        JavaParameter param;
        for (Object o : node.parameters()) {
            if (o instanceof VariableDeclaration) {
                VariableDeclaration var = (VariableDeclaration) o;

                param = new JavaParameter();
                param.setInput(true);
                param.setName(var.getName().getIdentifier());
                param.setTypeQualifiedName(var.resolveBinding().getType().getQualifiedName());
                param.setMethodQualifiedName(javaMethod.getQualifiedName());
                param.setQualifiedName(String.format("%s.%s", param.getMethodQualifiedName(), param.getName()));
                param.setParamSeq(seq);
                if (var instanceof SingleVariableDeclaration) {
                    SingleVariableDeclaration svd = (SingleVariableDeclaration) var;
                    Type paramType = svd.getType();
                    if (paramType.isParameterizedType()) {
                        ParameterizedType pt = (ParameterizedType) paramType;
                    }
                    param.setArray(paramType.isArrayType());
                }
                javaMethod.addParameter(param);
            }
            seq++;
        }
    }

    private void saveReturnParameter(IMethodBinding method, JavaMethod javaMethod) {
        JavaParameter param = new JavaParameter();
        param.setInput(false);
        param.setName("return");
        param.setTypeQualifiedName(method.getReturnType().getQualifiedName());
        param.setMethodQualifiedName(javaMethod.getQualifiedName());
        param.setQualifiedName(String.format("%s.%s", param.getMethodQualifiedName(), param.getName()));
        param.setParamSeq(-1);

        javaMethod.addParameter(param);
    }
}
