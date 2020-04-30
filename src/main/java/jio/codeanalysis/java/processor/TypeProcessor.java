package jio.codeanalysis.java.processor;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
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

        Optional<JavaType> result = createJavaType(type);
        if( result.isPresent() ) {
        	JavaType javaType = result.get();
            javaType.setStartPos(node.getStartPosition());
            javaType.setLength(node.getLength());
            javaType.setFilePath(filePath);

            em.merge(javaType);
        }

        return super.visit(node);
    }

    private Optional<JavaType> createJavaType(ITypeBinding type) {
        if( type != null ) {
            final JavaType javaType = new JavaType();
            javaType.setQualifiedName(type.getQualifiedName());
            javaType.setTypeName(type.getName());
            javaType.setIntrface(type.isInterface());

            createJavaType(type.getSuperclass()).ifPresent(javaType::setSuperClass);
            em.merge(javaType);
            em.flush();
            List<ITypeBinding> interfaces = Arrays.asList(type.getInterfaces());
            for(ITypeBinding t : interfaces) {
            	Optional<JavaType> intrface = createJavaType(t);
            	intrface.ifPresent(i -> {
            		JavaTypeRelation im = new JavaTypeRelation();
            		im.setImplementedClass(javaType);
            		im.setRealizeInterface(i);
            		javaType.addSuperInterface(im);
            		em.persist(im);
            	});
            }
            return Optional.of(javaType);
        }
        
        return Optional.ofNullable(null);
    }
    
    @Override
    public boolean visit(MethodDeclaration node) {
        IMethodBinding method = node.resolveBinding();

        if( method != null ) {
            JavaMethod javaMethod = new JavaMethod();
            javaMethod.setMethodName(method.getName());
            javaMethod.setQualifiedName(getMethodQualifiedName(method));

            // modifiers
            setModifiers(javaMethod, method);

            em.persist(javaMethod);
            // input parameter
            saveInputParameter(node, javaMethod);

            //output parameter
            saveReturnParameter(method, javaMethod);
        }

        node.getBody().accept(new MethodProcessor());
        return super.visit(node);
    }

    private void setModifiers(JavaMethod javaMethod, IMethodBinding method) {
        int modifiers = method.getModifiers();

        javaMethod.setFinal(Modifier.isFinal(modifiers));
        javaMethod.setStatic(Modifier.isStatic(modifiers));
        javaMethod.setAbstract(Modifier.isAbstract(modifiers));
        javaMethod.setPrivate(Modifier.isPrivate(modifiers));
        javaMethod.setPublic(Modifier.isPublic(modifiers));
        javaMethod.setProtected(Modifier.isProtected(modifiers));
    }

    private void saveInputParameter(MethodDeclaration node, JavaMethod javaMethod) {
        int seq = 0;
        JavaParameter param;
        for ( Object o : node.parameters() ) {
            if( o instanceof VariableDeclaration ) {
                VariableDeclaration var = (VariableDeclaration) o;

                param = new JavaParameter();
                param.setInput(true);
                param.setMethodQualifiedName(javaMethod.getQualifiedName());
                param.setParameterName(var.getName().getIdentifier());
                param.setTypeQualifiedName(var.resolveBinding().getType().getQualifiedName());
                param.setParamSeq(seq);
                if( var instanceof SingleVariableDeclaration ) {
                    SingleVariableDeclaration svd = (SingleVariableDeclaration) var;
                    Type paramType = svd.getType();
                    if( paramType.isParameterizedType() ) {
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
        for(Object o : node.arguments()) {
            logger.info(String.format("call arguments : %s", o.toString()));
            if( o instanceof SimpleName ) {
                SimpleName simpleName = (SimpleName) o;
                IBinding vb = simpleName.resolveBinding();
                if( vb != null ) {
                    //searchVariable(vb.getJavaElement(), vb.getJavaElement());
                }
            }
        }
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

            em.persist(callRelation);
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

    private void searchVariable(IJavaElement type, IJavaElement simpleName) {
        //IJavaSearchScope scope = SearchEngine.createJavaSearchScope(new IJavaElement[] { type });
        IJavaSearchScope scope = SearchEngine.createWorkspaceScope(); // Use this if you dont have the IProject in hand
        SearchPattern searchPattern = SearchPattern.createPattern(simpleName,
                IJavaSearchConstants.REFERENCES);
        SearchRequestor requestor = new SearchRequestor() {
            @Override
            public void acceptSearchMatch(SearchMatch match) {
                System.out.println(match.getElement());
            }
        };
        SearchEngine searchEngine = new SearchEngine();
        try {
            searchEngine.search(searchPattern,
                    new SearchParticipant[] { SearchEngine.getDefaultSearchParticipant() }, scope,
                    requestor, new NullProgressMonitor());
        } catch (CoreException e) {
            e.printStackTrace();
        }
    }
}
