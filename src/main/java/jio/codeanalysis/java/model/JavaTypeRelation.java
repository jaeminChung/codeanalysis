package jio.codeanalysis.java.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Table(name="java_type_relation")
@ToString
public class JavaTypeRelation {
	@Id
	@GeneratedValue
	private long id;
	
	@ManyToOne
	@JoinColumn(name="interface_id")
	private JavaType realizeInterface;
	
	@ManyToOne
	@JoinColumn(name="class_id")
	private JavaType implementedClass;
}
