package jio.codeanalysis.java.model;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Entity
@Table(name="java_type_relation")
@ToString(exclude = "implementedClass")
public class JavaTypeRelation {
	@Id
	@GeneratedValue
	private long id;
	
	@ManyToOne(fetch = FetchType.LAZY
	          ,cascade = CascadeType.ALL)
	@JoinColumn(name="interface_id")
	private JavaType realizeInterface;
	
	@ManyToOne
	@JoinColumn(name="class_id")
	private JavaType implementedClass;
}
