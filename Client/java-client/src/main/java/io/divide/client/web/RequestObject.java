package io.divide.client.web;

import static retrofit.Profiler.RequestInformation;

/**
 * Created by williamwebb on 4/9/14.
 */
public class RequestObject {
    public final RequestInformation info;
    public final long l;
    public final int i;
    public final Object o;

    public RequestObject(RequestInformation info,long l, int i, Object o){
        this.info = info;
        this.l = l;
        this.i = i;
        this.o = o;
    }
}
