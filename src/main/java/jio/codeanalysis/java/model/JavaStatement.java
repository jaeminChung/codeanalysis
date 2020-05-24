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
@Table(name = "java_statement")
@EqualsAndHashCode(of = "id")
@ToString(exclude = "javaMethod")
public class JavaStatement {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @Enumerated(EnumType.STRING)
    private StatementType statementType;

    private int startPos;
    private int length;
    private int loc;
    private String statement;

    @OneToMany(mappedBy = "javaStatement"
            , cascade = CascadeType.ALL)
    private List<JavaMethodInvocation> methodInvocations = new ArrayList<>();

    @ManyToOne
    private JavaMethod javaMethod;

    @ManyToOne
    @JoinColumn(name="parent_id")
    private JavaStatement parentStatement;

    @OneToMany(mappedBy = "parentStatement"
              , cascade = CascadeType.ALL)
    private List<JavaStatement> childStatements = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "sibling_id")
    private JavaStatement siblingStatement;

    @OneToMany(mappedBy = "siblingStatement"
            , cascade = CascadeType.ALL)
    private List<JavaStatement> siblingStatements = new ArrayList<>();

    public void addMethodInvocation(JavaMethodInvocation invocation) {
        invocation.setJavaStatement(this);
        invocation.setSeq(methodInvocations.size());
        methodInvocations.add(invocation);
    }

    public void addChildStatement(JavaStatement child) {
        child.setParentStatement(this);
        childStatements.add(child);
    }

    public void addSiblingStatement(JavaStatement sibiling) {
        sibiling.setSiblingStatement(this);
        siblingStatements.add(sibiling);
    }
}
