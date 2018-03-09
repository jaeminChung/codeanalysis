package jio.codeanalysis.java.processor;

import org.eclipse.jdt.core.dom.*;

import java.util.logging.Logger;

public class TypeProcessor extends ASTVisitor {
    private final static Logger logger = Logger.getLogger(TypeProcessor.class.getName());

    @Override
    public boolean visit(TypeDeclaration node) {
        ITypeBinding type = node.resolveBinding();

        if( type != null ) {
            /*
            JavaType javaType = new JavaType();
            javaType.setQualifiedName(type.getQualifiedName());
            javaType.setTypeName(type.getName());

            Session session = HibernateUtil.getSessionFactory().openSession();
            session.beginTransaction();
            session.save(javaType);
            session.getTransaction().commit();
            session.close();
            */
            logger.info(String.format("Type name : %s", type.getName()));
            logger.info(String.format("-- qualified name : %s", type.getQualifiedName()));
        }

        return super.visit(node);
    }

    @Override
    public boolean visit(MethodDeclaration node) {
        IMethodBinding method = node.resolveBinding();

        if( method != null ) {
            logger.info(String.format("\tMethod name : %s", method.getName()));
            logger.info(String.format("\t-- key : %s", method.getKey()));

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
            logger.info(String.format("\t\t\tMethod invocation : %s", method.getName()));
            logger.info(String.format("\t\t\tMethod's type qualified name : %s", method.getDeclaringClass().getQualifiedName()));
            logger.info(String.format("\t\t\tMethod invocation node : %s", node.toString()));
        }

        return super.visit(node);
    }

}
