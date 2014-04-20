package io.divide.client.data;

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
