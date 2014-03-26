package io.divide.shared.util;

import java.lang.reflect.*;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 8/25/13
 * Time: 8:22 AM
 */
public class ReflectionUtils {

    private ReflectionUtils(){} // never init

    public static Object getObjectField(Object o, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field f = getClassField(o.getClass(),fieldName);
        makeAccessible(f);
        return f.get(o);
    }

    public static void setObjectField(Object o, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field f = getClassField(o.getClass(),fieldName);
        makeAccessible(f);
        f.set(o,value);
    }

    public static Object invoke(Object o,Method method, Object... param) throws InvocationTargetException, IllegalAccessException {
        return method.invoke(o,param);
    }

    public static Method getObjectMethod(Object object, String methodName, Class<?>... paramsTypes) throws NoSuchMethodException {
        Method m = getClassMethod(object.getClass(),methodName, paramsTypes);
        makeAccessible(m);
        return m;
    }

    public static Method getClassMethod(Class clazz,String methodName, Class<?>... fieldParams) throws NoSuchMethodException {
        try {
            return clazz.getDeclaredMethod(methodName,fieldParams);
        } catch (NoSuchMethodException e) {
            Class superClass = clazz.getSuperclass();
            if (superClass == null) {
                throw e;
            } else {
                return getClassMethod(superClass,methodName,fieldParams);
            }
        }
    }

    public static Field getClassField(Class clazz, String fieldName) throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            Class superClass = clazz.getSuperclass();
            if (superClass == null) {
                throw e;
            } else {
                return getClassField(superClass, fieldName);
            }
        }
    }

    private static void makeAccessible(Method method) {
        if (!Modifier.isPublic(method.getModifiers()) ||
                !Modifier.isPublic(method.getDeclaringClass().getModifiers()))
        {
            method.setAccessible(true);
        }
    }

    private static void makeAccessible(Field field) {
        if (!Modifier.isPublic(field.getModifiers()) ||
                !Modifier.isPublic(field.getDeclaringClass().getModifiers()))
        {
            field.setAccessible(true);
        }
    }

    public static void setFinalStatic(Field field, Object newValue) throws Exception {
        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(null, newValue);
    }

    public static <Type> Type getFinalStatic(Class clazz, String fieldName, Class<Type> returnType) throws Exception {
        Field field = getClassField(clazz,fieldName);
        field.setAccessible(true);

//        Field modifiersField = Field.class.getDeclaredField("modifiers");
//        modifiersField.setAccessible(true);
//        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        return returnType.cast(field.get(null));
    }
}
