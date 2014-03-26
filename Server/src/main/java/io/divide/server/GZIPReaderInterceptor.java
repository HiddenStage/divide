package io.divide.server;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

/**
 * Created by williamwebb on 2/13/14.
 */
public class GZIPReaderInterceptor implements ReaderInterceptor {

    @Override
    public Object aroundReadFrom(ReaderInterceptorContext context)
            throws IOException, WebApplicationException {
        final InputStream originalInputStream = context.getInputStream();
        context.setInputStream(new GZIPInputStream(originalInputStream));
        return context.proceed();
    }
}