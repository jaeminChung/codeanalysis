package jio.codeanalysis.java.model;

import java.io.Serializable;

public class JavaParameterId implements Serializable {
	private static final long serialVersionUID = 1L;

	protected String methodQualifiedName;

    protected String parameterName;

    public JavaParameterId() {}

    public JavaParameterId(String methodQualifiedName, String parameterName) {
        this.methodQualifiedName = methodQualifiedName;
        this.parameterName = parameterName;
    }

}
