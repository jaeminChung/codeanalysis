package jio.codeanalysis.java.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name="java_method_invocation")
@EqualsAndHashCode
@ToString(exclude = "javaStatement")
public class JavaMethodInvocation {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String callerQualifiedName;

    private String calleeQualifiedName;

    private int seq;

    @ElementCollection
    @CollectionTable(name="java_method_invocation_input", joinColumns = @JoinColumn(name="statement_id"))
    //@Column(name="input_value")
    private List<String> inputValues = new ArrayList<>();

    @ManyToOne
    private JavaStatement javaStatement;

    public void addInputValue(String value) {
        inputValues.add(value);
    }
}
