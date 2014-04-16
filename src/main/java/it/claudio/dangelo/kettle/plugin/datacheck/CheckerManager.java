package it.claudio.dangelo.kettle.plugin.datacheck;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

public class CheckerManager {
	
	private volatile static Map<String, CheckerExecutor> checkerExecutors = null;
	
	public static String[] getCheckerNames() throws Exception {
		if(checkerExecutors == null) init();
		return checkerExecutors.keySet().toArray(new String[checkerExecutors.size()]);
	}
	
	public static CheckerExecutor getCheckerExecutor(String id) throws Exception {
		if(checkerExecutors == null) init();
		return checkerExecutors.get(id);
	}
	
	public static List<String> validate(Map<String, Object> row, CheckElement[] elements) throws Exception {
		ArrayList<String> errors = new ArrayList<String>();
		for (CheckElement checkElement : elements) {
			String error = validate(row, checkElement);
			if(error == null) continue;
			errors.add(error);
		}
		return errors;
		
	}
	
	public static String validate(Map<String, Object> row, CheckElement element) throws Exception {
		if(checkerExecutors == null) init();
		CheckerExecutor checkerExecutor = checkerExecutors.get(element.getChecker());
		String fieldName = element.getField();
		String parameter = element.getParameter();
		String message = element.getMessage();
		if(message == null) message = checkerExecutor.defaultMessage; 
		Object result = checkerExecutor.method.invoke(checkerExecutor.checker, fieldName, row, parameter, message);
		if(result == null) return null;
		if(result instanceof String)
			return (String) result;
		else
			return result.toString();
	}
	
	
	private synchronized static void init() throws Exception {
		if(checkerExecutors != null) return;
		HashMap<String, Object> checkers = new HashMap<String, Object>();
		InputStream stream = null;
		try {
			stream = CheckerManager.class.getClassLoader().getResourceAsStream("/META-INF/data-validation/DefaultsChecker.dv");
			elabConfig(checkers, stream);
		} finally {
			if(stream != null) try {stream.close();} catch (Exception e) {}
		}
		Enumeration<URL> resources = CheckerManager.class.getClassLoader().getResources("/META-INF/data-validation/Checker.dv");
		while (resources.hasMoreElements()) {
			URL url = (URL) resources.nextElement();
			try {
				stream = url.openStream();
				elabConfig(checkers, stream);
			} finally {
				if(stream != null) try {stream.close();} catch (Exception e) {}
			}
		}
	}


	private static void elabConfig(HashMap<String, Object> checkers,
			InputStream stream) throws IOException, InstantiationException,
			IllegalAccessException {
		BufferedReader br = new BufferedReader(new InputStreamReader(stream));
		String line = br.readLine();
		while(line != null) {
			String pkg = line;
			scanPackage(checkers, pkg);
		}
	}


	private static void scanPackage(HashMap<String, Object> checkers, String pkg)
			throws InstantiationException, IllegalAccessException {
		Reflections reflections = new Reflections(pkg);
		Set<Class<?>> types = reflections.getTypesAnnotatedWith(Checker.class);
		for (Class<?> class1 : types) {
			resolveClass(checkers, class1);
		}
	}


	private static void resolveClass(HashMap<String, Object> checkers,
			Class<?> class1) throws InstantiationException,
			IllegalAccessException {
		String idChecker = class1.getAnnotation(Checker.class).id();
		Object checker = class1.newInstance();
		resolveMethods(checkers, class1, idChecker, checker);
	}


	private static void resolveMethods(HashMap<String, Object> checkers,
			Class<?> class1, String idChecker, Object checker) {
		Method[] methods = class1.getMethods();
		for (Method method : methods) {
			if(!Modifier.isPublic(method.getModifiers())) continue;
			Class<?>[] parameterTypes = method.getParameterTypes();
			if( parameterTypes.length != 4 &&
				!String.class.isAssignableFrom(parameterTypes[0]) && 
				!Map.class.isAssignableFrom(parameterTypes[1]) &&
				!String.class.isAssignableFrom(parameterTypes[2]) &&
				!String.class.isAssignableFrom(parameterTypes[3])) continue;
			if(!method.isAnnotationPresent(CheckFunction.class)) continue;
			CheckFunction annotation = method.getAnnotation(CheckFunction.class);
			CheckerExecutor executor = new CheckerExecutor(idChecker+"."+annotation.name(), checker, method, annotation.defaultMessage());
			checkers.put(executor.id, executor);
		}
	}
	
	
	public static class CheckerExecutor {
		public String id;
		public Object checker;
		public Method method;
		public String defaultMessage;
		public CheckerExecutor(String id, Object checker, Method method,
				String defaultMessage) {
			super();
			this.id = id;
			this.checker = checker;
			this.method = method;
			this.defaultMessage = defaultMessage;
		}
		
		
	}
}
