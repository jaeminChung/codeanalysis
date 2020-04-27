package jio.codeanalysis.java.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table( name="call_relation" )
public class CallRelation implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
    @Column( name="caller" )
    private String caller;

    @Id
    @Column( name="callee" )
    private String callee;

    public String getCaller() {
        return caller;
    }

    public void setCaller(String caller) {
        this.caller = caller;
    }

    public String getCallee() {
        return callee;
    }

    public void setCallee(String callee) {
        this.callee = callee;
    }
}
