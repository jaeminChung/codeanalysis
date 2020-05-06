package jio.codeanalysis.java.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "java_statement")
@EqualsAndHashCode(of = "id")
@ToString
public class JavaStatement {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @ManyToOne
    @JoinColumn(name="qualifiedName")
    private JavaMethod javaMethod;

    @ManyToOne
    @JoinColumn(name="parent_id")
    private JavaStatement parentStatement;

    @OneToMany(mappedBy = "parentStatement"
              , cascade = CascadeType.ALL)
    private List<JavaStatement> childStatements;

    private String filePath;
    private int startPos;
    private int length;
    private int loc;
    private String statement;
}
