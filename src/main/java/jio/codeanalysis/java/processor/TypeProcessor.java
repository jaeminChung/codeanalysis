package jio.codeanalysis.java.processor;

import jio.codeanalysis.java.model.CallRelation;
import jio.codeanalysis.java.model.JavaMethod;
import jio.codeanalysis.java.model.JavaParameter;
import jio.codeanalysis.java.model.JavaType;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.*;
import org.eclipse.jdt.core.search.*;
import org.hibernate.Session;

import java.util.StringJoiner;
import java.util.logging.Logger;

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
            javaMethod.setQualifiedName(getMethodQualifiedName(method));

            // modifiers
            setModifiers(javaMethod, method);

            session.saveOrUpdate(javaMethod);
            // input parameter
            saveInputParameter(node, javaMethod);

            //output parameter
            saveReturnParameter(method, javaMethod);
        }

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
                IVariableBinding var = ((VariableDeclaration) o).resolveBinding();
                if( var != null ) {
                    param = new JavaParameter();
                    param.setInput(true);
                    param.setMethodQualifiedName(javaMethod.getQualifiedName());
                    param.setParameterName(var.getName());
                    param.setTypeQualifiedName(var.getType().getQualifiedName());
                    param.setParamSeq(seq);

                    session.saveOrUpdate(param);
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

        session.saveOrUpdate(param);
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
                    searchVariable(vb.getJavaElement(), vb.getJavaElement());
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
