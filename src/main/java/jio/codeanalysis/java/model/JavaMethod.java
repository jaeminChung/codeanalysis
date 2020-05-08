package jio.codeanalysis.java.model;

import javax.persistence.*;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.Modifier;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name="java_method")
@EqualsAndHashCode(of = "qualifiedName")
@ToString
public class JavaMethod {
    @Id
    private String qualifiedName;

    private String methodName;

    private boolean isFinal;

    private boolean isStatic;

    private boolean isAbstract;

    private boolean isPublic;

    private boolean isPrivate;

    private boolean isProtected;

    @OneToMany(mappedBy = "javaMethod", cascade = CascadeType.ALL)
    private List<JavaStatement> statements = new ArrayList<>();

    @OneToMany(mappedBy = "javaMethod", cascade = CascadeType.ALL)
    private List<JavaParameter> parameters = new ArrayList<>();

    public void setModifiers(IMethodBinding method) {
        int modifiers = method.getModifiers();

        setFinal(Modifier.isFinal(modifiers));
        setStatic(Modifier.isStatic(modifiers));
        setAbstract(Modifier.isAbstract(modifiers));
        setPrivate(Modifier.isPrivate(modifiers));
        setPublic(Modifier.isPublic(modifiers));
        setProtected(Modifier.isProtected(modifiers));
    }

    public void addStatement(JavaStatement statement) {
        statement.setJavaMethod(this);
        statements.add(statement);
    }

    public void addParameter(JavaParameter parameter) {
        parameter.setJavaMethod(this);
        parameters.add(parameter);
    }
}
