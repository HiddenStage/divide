package com.jug6ernaut.network.authenticator.server.utils;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 9/4/13
 * Time: 6:46 PM
 */
public class ObjectUtils {

    public static <T> T get1stOrNull(Collection<T> objects){
        return (objects!=null && objects.size() == 1)? (T) objects.toArray()[0] :null;
    }

}

