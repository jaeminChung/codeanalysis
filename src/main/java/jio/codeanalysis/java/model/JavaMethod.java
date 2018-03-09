package jio.codeanalysis.java.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="java_method")
public class JavaMethod {
    @Id
    @Column(name="quilified_name")
    private String quilifiedName;

    @Column(name="method_name")
    private String methodName;

    public String getQuilifiedName() {
        return quilifiedName;
    }

    public void setQuilifiedName(String quilifiedName) {
        this.quilifiedName = quilifiedName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }
}
