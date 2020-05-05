package jio.codeanalysis.java.processor;

import java.util.*;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import jio.codeanalysis.java.model.JavaType;
import jio.codeanalysis.java.model.JavaTypeRelation;

public class TypeProcessor extends ASTVisitor {
    private final static Logger logger = Logger.getLogger(TypeProcessor.class.getName());
    private final EntityManager em;
    private final String filePath;

    public TypeProcessor(EntityManager em, String filePath) {
        this.em = em;
        this.filePath = filePath;
    }
    
    @Override
    public boolean visit(TypeDeclaration node) {
        ITypeBinding type = node.resolveBinding();

        createJavaType(type, node, filePath).ifPresent(em::merge);


        return super.visit(node);
    }

    private Optional<JavaType> createJavaType(ITypeBinding type) {
        return createJavaType(type, null, null);
    }
    private Optional<JavaType> createJavaType(ITypeBinding type,TypeDeclaration node, String filePath) {
        if(Objects.nonNull(type)) {
            JavaType javaType = new JavaType();
            javaType.setQualifiedName(type.getQualifiedName());
            javaType.setTypeName(type.getName());
            javaType.setInterface(type.isInterface());

            createJavaType(type.getSuperclass()).ifPresent(javaType::setSuperClass);
            javaType.setFileInfo(node, filePath);

            ITypeBinding[] interfaces = type.getInterfaces();
            for(ITypeBinding t : interfaces) {
            	Optional<JavaType> interfac = createJavaType(t);
            	interfac.ifPresent(i -> {
            		JavaTypeRelation im = new JavaTypeRelation();
            		im.setImplementedClass(javaType);
            		im.setRealizeInterface(i);
            		javaType.addSuperInterface(im);
            	});
            }
            return Optional.of(javaType);
        }
        
        return Optional.empty();
    }
    
    @Override
    public boolean visit(MethodDeclaration node) {
        node.accept(new MethodProcessor(em, filePath));
        return super.visit(node);
    }
}
