package io.divide.client.web;

import retrofit.Profiler;

/**
 * Created by williamwebb on 4/9/14.
 */
public class RequestObject {
    public final Profiler.RequestInformation info;
    public final long l;
    public final int i;
    public final Object o;

    public RequestObject(Profiler.RequestInformation info,long l, int i, Object o){
        this.info = info;
        this.l = l;
        this.i = i;
        this.o = o;
    }
}
