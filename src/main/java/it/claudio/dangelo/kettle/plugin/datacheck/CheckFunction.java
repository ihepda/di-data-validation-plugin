package it.claudio.dangelo.kettle.plugin.datacheck;

public @interface CheckFunction {
	String name();
	String description();
	String defaultMessage();
}
