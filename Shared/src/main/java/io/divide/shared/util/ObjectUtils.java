/*
 * Copyright (C) 2014 Divide.io
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.divide.shared.util;

import java.lang.reflect.Array;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

public class ObjectUtils {

    public static <T> Collection<T> v2c(T... t){
        return Arrays.asList(t);
    }

    public static  <T> T[] c2v(Collection<T> t){
        Class c = t.iterator().next().getClass();
        T[] tsa = (T[]) Array.newInstance(c, 0);
        return t.toArray(tsa);
    }

    public static <T> T get1stOrNull(T... objects){
        return (objects!=null && objects.length == 1)? (T) objects[0] : null;
    }

    public static <T> T get1stOrNull(Collection<T> objects){
        return (objects!=null && objects.size() == 1) ? c2v(objects)[0] : null;
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

    public static <T> T[] toArray(Class<T> t, Iterable<T> elements)
    {
        final ArrayList<T> arrayElements = new ArrayList<T>();
        for (T element : elements)
        {
            arrayElements.add(element);
        }
        final T[] ta = (T[]) Array.newInstance(t, arrayElements.size());
        return arrayElements.toArray(ta);
    }


    public static boolean isCollection(Object o){
        return isCollection(o.getClass());
    }

    public static boolean isMap(Object o){
        return isMap(o.getClass());
    }

    public static boolean isArray(Object o){
        return isArray(o.getClass());
    }

    public static boolean isCollection(Class<?> o){
        return Collection.class.isAssignableFrom(o);
    }

    public static boolean isMap(Class<?> o){
        return Map.class.isAssignableFrom(o);
    }

    public static boolean isArray(Class<?> o){
        return Object[].class.isAssignableFrom(o);
    }

}
