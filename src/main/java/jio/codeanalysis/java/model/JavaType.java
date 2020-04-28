package jio.codeanalysis.java.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
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
}
