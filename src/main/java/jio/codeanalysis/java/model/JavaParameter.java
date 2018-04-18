package jio.codeanalysis.java.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name="java_parameter")
@IdClass(JavaParameterId.class)
public class JavaParameter implements Serializable {
    @Id
    @Column(name="method_qualified_name")
    private String methodQualifiedName;

    @Id
    @Column(name="parameter_name")
    private String parameterName;

    @Column(name="type_qualified_name")
    private String typeQualifiedName;

    @Column(name="is_input")
    private boolean isInput;

    @Column(name="param_seq")
    private int paramSeq;

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public String getMethodQualifiedName() {
        return methodQualifiedName;
    }

    public void setMethodQualifiedName(String methodQualifiedName) {
        this.methodQualifiedName = methodQualifiedName;
    }

    public String getTypeQualifiedName() {
        return typeQualifiedName;
    }

    public void setTypeQualifiedName(String typeQualifiedName) {
        this.typeQualifiedName = typeQualifiedName;
    }

    public boolean isInput() {
        return isInput;
    }

    public void setInput(boolean input) {
        isInput = input;
    }

    public int getParamSeq() {
        return paramSeq;
    }

    public void setParamSeq(int paramSeq) {
        this.paramSeq = paramSeq;
    }
}