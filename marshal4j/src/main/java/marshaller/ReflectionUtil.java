package marshaller;

import java.lang.reflect.InvocationTargetException;

public class ReflectionUtil {
	
	public static Class<?> classFrom(String name) {
		Class<?> clazz = null;
		try {
			clazz = Class.forName(name);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return clazz;
	}
	
	public static Object instanceFrom(String name) {
		Class<?> clazz = classFrom(name);
		
		Object obj = null;
		try {
			obj = clazz.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return obj;
	}
	
	public static void invokeMethod(Object parent, String method, Class<?>[] paramTypes, Object[] args) {
		try {
			parent.getClass().getMethod(method, paramTypes).invoke(parent, args);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}
}
