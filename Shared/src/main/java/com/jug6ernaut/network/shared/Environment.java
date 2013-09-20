package com.jug6ernaut.network.shared;

import com.jug6ernaut.network.shared.util.ReflectionUtils;

import java.lang.reflect.Field;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 8/24/13
 * Time: 10:22 PM
 */
public enum Environment {

        SERVER,
        CLIENT;

    private static Environment curEnv;

    public static Environment getCurrent() {
        if (curEnv == null) { //Expensive Operation, dont do so more then needed.
            try {

                Field envField = ReflectionUtils.getClassField(
                        Class.forName("com.jug6ernaut.network.Env"),
                        "ENVIRONMENT");
                curEnv = (Environment) envField.get(null);

            } catch (Exception e) {
                e.printStackTrace();
                throw new IllegalStateException(
                        "Class is attempting to be run on without blah blah blah, fix me");
                //TODO better error message, even tho this should be an impossible state
            }
        }
        return curEnv;
    }

}
