package jio.codeanalysis.java.processor;

import java.util.*;
import java.util.logging.Logger;

import javax.persistence.EntityManager;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.IBinding;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclaration;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchMatch;
import org.eclipse.jdt.core.search.SearchParticipant;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jdt.core.search.SearchRequestor;

import jio.codeanalysis.java.model.CallRelation;
import jio.codeanalysis.java.model.JavaMethod;
import jio.codeanalysis.java.model.JavaParameter;
import jio.codeanalysis.java.model.JavaType;
import jio.codeanalysis.java.model.JavaTypeRelation;

public class TypeProcessor extends ASTVisitor {
    private final static Logger logger = Logger.getLogger(TypeProcessor.class.getName());
    private EntityManager em;
    private final String filePath;

    public TypeProcessor(EntityManager em, String filePath) {
        this.em = em;
        this.filePath = filePath;
    }
    
    @Override
    public boolean visit(TypeDeclaration node) {
        ITypeBinding type = node.resolveBinding();

        createJavaType(type, node, filePath);

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
            javaType.setIntrface(type.isInterface());

            createJavaType(type.getSuperclass()).ifPresent(javaType::setSuperClass);
            javaType.setNodeInfo(node);
            javaType.setFilePath(filePath);

            em.merge(javaType);
            em.flush();

            ITypeBinding[] interfaces = type.getInterfaces();
            for(ITypeBinding t : interfaces) {
            	Optional<JavaType> interfaze = createJavaType(t);
            	interfaze.ifPresent(i -> {
            		JavaTypeRelation im = new JavaTypeRelation();
            		im.setImplementedClass(javaType);
            		im.setRealizeInterface(i);
            		javaType.addSuperInterface(im);
            		em.persist(im);
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
