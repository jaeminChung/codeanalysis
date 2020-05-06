package jio.codeanalysis.java.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table( name="call_relation" )
@ToString
public class CallRelation implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
    @Column( name="caller" )
    private String caller;

    @Id
    @Column( name="callee" )
    private String callee;

    //input value list
    //constructor, method call
}
