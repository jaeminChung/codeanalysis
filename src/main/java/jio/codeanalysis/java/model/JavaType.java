package jio.codeanalysis.java.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.*;

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
    private boolean isInterface;
    
    @OneToMany(mappedBy="realizeInterface"
              ,cascade = CascadeType.ALL)
    private List<JavaTypeRelation> superInterfaces = new ArrayList<>();
    
    @ManyToOne(fetch = FetchType.LAZY
              ,cascade = CascadeType.ALL)
    @JoinColumn(name="superclass_id")
    private JavaType superClass;

    @OneToOne(mappedBy = "type"
             ,cascade = CascadeType.ALL)
    private JavaTypeFileInfo fileInfo;

    public void addSuperInterface(JavaTypeRelation relation) {
    	this.superInterfaces.add(relation);
    }

    public void setFileInfo(TypeDeclaration node, String filePath) {
        if(Objects.nonNull(node)) {
            fileInfo = new JavaTypeFileInfo();
            fileInfo.setStartPos(node.getStartPosition());
            fileInfo.setLength(node.getLength());
            fileInfo.setFilePath(filePath);
            fileInfo.setJavaType(this);
        }
    }
}
