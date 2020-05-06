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
@EqualsAndHashCode(of = "paramQualifiedName")
@ToString
public class JavaParameter {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Column(name="method_qualified_name")
    private String methodQualifiedName;

    @Column(name="parameter_name")
    private String parameterName;

    @Column(name="type_qualified_name")
    private String typeQualifiedName;

    @Column(name="param_qualified_name")
    private String paramQualifiedName;
    
    @Column(name="is_array")
    private boolean isArray;
    
    @Column(name="is_input")
    private boolean isInput;

    @Column(name="param_seq")
    private int paramSeq;
}
