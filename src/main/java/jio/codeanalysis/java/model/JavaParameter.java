package jio.codeanalysis.java.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name="java_parameter")
@EqualsAndHashCode(of = "qualifiedName")
@ToString(exclude = "javaMethod")
public class JavaParameter {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String methodQualifiedName;

    private String name;

    private String typeQualifiedName;

    private String qualifiedName;
    
    private boolean isArray;
    
    private boolean isInput;

    private int paramSeq;

    @ManyToOne
    private JavaMethod javaMethod;
}
