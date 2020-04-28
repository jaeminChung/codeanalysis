package jio.codeanalysis.java.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

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
}
