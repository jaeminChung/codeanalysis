package jio.codeanalysis.util;

import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;

import java.util.StringJoiner;

public class ASTUtil {
    public static String getMethodQualifiedName(IMethodBinding method) {
        String typeQualifiedName = method.getDeclaringClass().getQualifiedName();
        String methodName = method.getName() + getParameters(method);

        return String.format("%s.%s", typeQualifiedName, methodName);
    }

    public static String getMethodQualifiedName(MethodDeclaration node) {
        String qualifiedName = "";

        IMethodBinding method = node.resolveBinding();
        if (method != null) {
            qualifiedName = getMethodQualifiedName(method);
        }

        return qualifiedName;
    }

    private static String getParameters(IMethodBinding method) {

        StringJoiner joiner = new StringJoiner(",");
        for (ITypeBinding tb : method.getParameterTypes()) {
            joiner.add(tb.getName());
        }
        return String.format("(%s)", joiner.toString());
    }


}
