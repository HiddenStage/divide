package com.jug6ernaut.network.shared.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 9/2/13
 * Time: 11:06 AM
 */
public class ObjectUtils {

    public static  <T> Collection<T> v2c(T... t){
        List<T> ts = new ArrayList<T>(Arrays.asList(t));
        return ts;
    }

    public static  <T> T[] c2v(Collection<T> t){
        T[] ts = (T[]) t.toArray();
        return ts;
    }

    public static <T> T get1stOrNull(Collection<T> objects){
        return (objects!=null && objects.size() == 1)? (T) objects.toArray()[0] :null;
    }

    public static Class<?> getType(Class<?> clazz){

        Class<?> clazz3 = clazz;
        do{
            clazz3 = (Class<?>) clazz3.getGenericSuperclass();
            System.out.println(clazz3);
        }while (clazz3!=null);

        System.out.println("givenClass: " + clazz);
        Type clazz2 = clazz.getGenericSuperclass();
        System.out.println("superClass: " + clazz2);
        ParameterizedType pType = (ParameterizedType)clazz2;
        System.out.println("pType: " + pType);
        Class<?> persistentClass = (Class<?>) pType.getActualTypeArguments()[0];
        System.out.println("type: " + persistentClass);
//        Class<?> persistentClass = (Class<?>) ((ParameterizedType) clazz.getGenericSuperclass()).getActualTypeArguments()[0];
        return persistentClass;
    }
}
