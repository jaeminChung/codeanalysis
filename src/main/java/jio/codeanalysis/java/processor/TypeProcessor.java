package jio.codeanalysis.java.processor;

import jio.codeanalysis.java.model.CallRelation;
import jio.codeanalysis.java.model.JavaMethod;
import jio.codeanalysis.java.model.JavaType;
import org.eclipse.jdt.core.dom.*;
import org.hibernate.Session;

import java.util.logging.Logger;
import java.util.StringJoiner;

public class TypeProcessor extends ASTVisitor {
    private final static Logger logger = Logger.getLogger(TypeProcessor.class.getName());
    private SourceScanner scanner;
    private Session session;

    public TypeProcessor(Session session, SourceScanner scanner) {
        this.session = session;
        this.scanner = scanner;
    }
    @Override
    public boolean visit(TypeDeclaration node) {
        ITypeBinding type = node.resolveBinding();

        if( type != null ) {
            JavaType javaType = new JavaType();
            javaType.setQualifiedName(type.getQualifiedName());
            javaType.setTypeName(type.getName());

            session.saveOrUpdate(javaType);
        }

        return super.visit(node);
    }

    @Override
    public boolean visit(MethodDeclaration node) {
        IMethodBinding method = node.resolveBinding();

        if( method != null ) {
            JavaMethod javaMethod = new JavaMethod();
            javaMethod.setMethodName(method.getName());
            javaMethod.setQualifiedName(method.getKey());

            session.saveOrUpdate(javaMethod);
            // modifiers
            int modifiers = method.getModifiers();
            //Modifier.isFinal(modifiers);
            //Modifier.isStatic(modifiers);

            // input parameter
            int index = 0;
            for ( Object o : node.parameters() ) {
                if( o instanceof VariableDeclaration ) {
                    IVariableBinding var = ((VariableDeclaration) o).resolveBinding();
                    logger.info(String.format("\t\t#%s Parameter Type : %s", index, var.getType().getQualifiedName()));
                    logger.info(String.format("\t\t#%s Parameter Name : %s", index, var.getName()));
                }
                index++;
            }

            //output parameter
            logger.info(String.format("\t\tReturn Type : %s", method.getReturnType().getQualifiedName()));

        }

        return super.visit(node);
    }

    @Override
    public boolean visit(MethodInvocation node) {
        IMethodBinding method = node.resolveMethodBinding();

        if( method != null ) {
            ASTNode parent = node.getParent();
            while( (parent instanceof MethodDeclaration) == false ) {
                parent = parent.getParent();
            }
            String caller = getMethodQualifiedName( (MethodDeclaration) parent );
            String callee = getMethodQualifiedName( method );

            CallRelation callRelation = new CallRelation();
            callRelation.setCaller(caller);
            callRelation.setCallee(callee);

            session.saveOrUpdate(callRelation);
        }

        return super.visit(node);
    }

    private String getMethodQualifiedName(MethodDeclaration node) {
        String qualifiedName = "";

        IMethodBinding method = node.resolveBinding();
        if( method != null ) {
            qualifiedName = getMethodQualifiedName(method);
        }

        return qualifiedName;
    }

    private String getMethodQualifiedName(IMethodBinding method) {
        String typeQualifiedName = method.getDeclaringClass().getQualifiedName();
        String methodName = method.getName() + getParameters(method);

        return String.format("%s.%s", typeQualifiedName, methodName);
    }

    private String getParameters(IMethodBinding method) {
        StringJoiner joiner = new StringJoiner(",");
        for(ITypeBinding tb : method.getParameterTypes()) {
            joiner.add(tb.getName());
        }
        return String.format("(%s)", joiner.toString());
    }
}
