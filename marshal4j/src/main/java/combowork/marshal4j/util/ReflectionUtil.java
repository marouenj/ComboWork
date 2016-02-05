package combowork.marshal4j.util;

public class ReflectionUtil {

    public static Class<?> classFrom(String name) {
        Class<?> clazz;
        try {
            clazz = Class.forName(name);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e.getMessage());
        }
        return clazz;
    }

    public static Object instanceFrom(String name) {
        Class<?> clazz = classFrom(name);

        Object obj;
        try {
            obj = clazz.newInstance();
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e.getMessage());
        }
        return obj;
    }

    public static void invokeMethod(Object parent, String method, Class<?>[] paramTypes, Object[] args) {
        try {
            parent.getClass().getMethod(method, paramTypes).invoke(parent, args);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
