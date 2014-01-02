package com.jug6ernaut.network.authenticator.client;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created by williamwebb on 12/5/13.
 */
public class Result<T> {
    Future<T> future;
    public Result(Future<T> future){
        this.future = future;
    }

    public T now()
    {
        try
        {
            return this.future.get();
        }
        catch (Exception e)
        {
            unwrapAndThrow(e);
            return null;    // make compiler happy
        }
    }

    private static void unwrapAndThrow(Throwable ex)
    {
        if (ex instanceof RuntimeException)
            throw (RuntimeException)ex;
        else if (ex instanceof Error)
            throw (Error)ex;
        else if (ex instanceof ExecutionException)
            unwrapAndThrow(ex.getCause());
        else
            throw new UndeclaredThrowableException(ex);
    }

}
