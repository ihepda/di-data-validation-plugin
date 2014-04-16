package it.claudio.dangelo.kettle.plugin.datacheck.plugins;

import it.claudio.dangelo.kettle.plugin.datacheck.CheckFunction;
import it.claudio.dangelo.kettle.plugin.datacheck.Checker;

import java.text.MessageFormat;
import java.util.Map;

@Checker(id="string")
public class StringChecker {
	
	@CheckFunction(name="length", description="Check a string length", defaultMessage="Invalid length string for field {0}. The length must be {3}")
	public String checkLength(String fieldName, Map<String, Object> row, String parameter, String message) {
		if(parameter == null || parameter.length() == 0) return null;
		int lengthParam = Integer.parseInt(parameter);
		Object value = row.get(fieldName);
		if(value == null) return null;
		if(!(value instanceof String)) return null;
		String string = (String) value;
		int length = string.length();
		if(length != lengthParam)
			return MessageFormat.format(message, fieldName, value, length, lengthParam);
		return null;
	}

}
