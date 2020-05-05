package jio.codeanalysis.java.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "java_type_file_info")
@EqualsAndHashCode(of = "javaType")
@ToString(exclude = "javaType")
public class JavaTypeFileInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    @OneToOne
    @JoinColumn(name="qualifiedName")
    private JavaType javaType;

    private String filePath;
    private int startPos;
    private int length;
    private int loc;
}
