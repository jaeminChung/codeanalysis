package jio.codeanalysis.java.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
import org.eclipse.jdt.core.dom.TypeDeclaration;

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

    public void setNodeInfo(TypeDeclaration node) {
        if(Objects.nonNull(node)) {
            setStartPos(node.getStartPosition());
            setLength(node.getLength());
        }
    }
}
