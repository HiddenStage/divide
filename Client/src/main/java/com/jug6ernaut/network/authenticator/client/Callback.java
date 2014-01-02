package com.jug6ernaut.network.authenticator.client;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 8/28/13
 * Time: 3:53 PM
 */
public interface Callback<T> {
    public void onResult(T t);
    public void onError(Exception t);
}
