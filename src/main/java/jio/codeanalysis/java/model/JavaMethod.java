package jio.codeanalysis.java.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.Modifier;

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

    public void setModifiers(IMethodBinding method) {
        int modifiers = method.getModifiers();

        setFinal(Modifier.isFinal(modifiers));
        setStatic(Modifier.isStatic(modifiers));
        setAbstract(Modifier.isAbstract(modifiers));
        setPrivate(Modifier.isPrivate(modifiers));
        setPublic(Modifier.isPublic(modifiers));
        setProtected(Modifier.isProtected(modifiers));
    }
}
