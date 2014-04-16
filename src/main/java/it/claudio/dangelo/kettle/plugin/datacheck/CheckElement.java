package it.claudio.dangelo.kettle.plugin.datacheck;


public class CheckElement {
	private String field;
	private String checker;
	private String parameter;
	private String message;
	public CheckElement(String field, String checker, String parameter) {
		super();
		this.field = field;
		this.checker = checker;
		this.parameter = parameter;
	}
	
	
	public CheckElement(String field, String checker, String parameter,
			String message) {
		super();
		this.field = field;
		this.checker = checker;
		this.parameter = parameter;
		this.message = message;
	}


	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public String getChecker() {
		return checker;
	}
	public void setChecker(String checker) {
		this.checker = checker;
	}
	public String getParameter() {
		return parameter;
	}
	public void setParameter(String parameter) {
		this.parameter = parameter;
	}


	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
	}
	
	
	
	
	
}
