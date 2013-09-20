package com.jug6ernaut.network.shared.util;

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
}
