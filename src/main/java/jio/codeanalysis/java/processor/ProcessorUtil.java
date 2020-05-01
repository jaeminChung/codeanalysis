package jio.codeanalysis.java.processor;

import jio.codeanalysis.java.model.JavaMethod;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.search.*;

public class ProcessorUtil {
    public static void setModifiers(JavaMethod javaMethod, IMethodBinding method) {
        int modifiers = method.getModifiers();

        javaMethod.setFinal(Modifier.isFinal(modifiers));
        javaMethod.setStatic(Modifier.isStatic(modifiers));
        javaMethod.setAbstract(Modifier.isAbstract(modifiers));
        javaMethod.setPrivate(Modifier.isPrivate(modifiers));
        javaMethod.setPublic(Modifier.isPublic(modifiers));
        javaMethod.setProtected(Modifier.isProtected(modifiers));
    }

    public static void searchVariable(IJavaElement type, IJavaElement simpleName) {
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
                    new SearchParticipant[]{SearchEngine.getDefaultSearchParticipant()}, scope,
                    requestor, new NullProgressMonitor());
        } catch (CoreException e) {
            e.printStackTrace();
        }
    }
}
