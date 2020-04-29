package jio.codeanalysis.java.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Table(name="java_type")
@EqualsAndHashCode(of = "qualifiedName")
@ToString
public class JavaType {
    @Id
    private String qualifiedName;

    private String typeName;
    private String filePath;
    private int startPos;
    private int length;
    private boolean intrface;
    
    @OneToMany(mappedBy="realizeInterface")
    private List<JavaTypeRelation> superInterfaces = new ArrayList<>();
    
    @ManyToOne
    @JoinColumn(name="superclass_id")
    private JavaType superClass;
    
    public void addSuperInterface(JavaTypeRelation relation) {
    	this.superInterfaces.add(relation);
    }
}
