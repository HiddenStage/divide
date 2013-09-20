package com.jug6ernaut.network.authenticator.server.dao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jug6ernaut.network.shared.web.transitory.Credentials;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.logging.Logger;

@Provider
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CredentialBodyHandler<T extends Credentials> implements MessageBodyWriter<T> {

    public static final Logger logger = Logger.getLogger(CredentialBodyHandler.class.getName());
    private static final String UTF_8 = "UTF-8";

    private Gson gson;
    private SecurityContext context;

    public CredentialBodyHandler(@Context SecurityContext context){
        this.context = context;
    }

    private Gson getGson() {
        if (gson == null) {
            final GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.enableComplexMapKeySerialization();
            gsonBuilder.setPrettyPrinting();
            gson = gsonBuilder.create();
        }
        return gson;
    }

    @Override
    public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return Credentials.class.isAssignableFrom(type);
    }

    @Override
    public long getSize(T object, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
        return -1;
    }



    @Override
    public void writeTo(T object, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException, WebApplicationException {
        OutputStreamWriter writer = new OutputStreamWriter(entityStream, UTF_8);

        try {
            Type jsonType;
            if (type.equals(genericType)) {
                jsonType = type;
            } else {
                jsonType = genericType;
            }

            // if the object != null and emails dont match
            if(!match(object,context)){
                getGson().toJson(object.getSafe(), jsonType, writer);

            } else {
                getGson().toJson(object, jsonType, writer);
            }

        } finally {
            writer.close();
        }
    }

    private static final boolean match( Credentials credentials, SecurityContext context){
        if(credentials == null ||
           credentials.getEmailAddress() == null ||
           context == null ||
           context.getUserPrincipal() == null ||
           context.getUserPrincipal().getName() == null){

            return false;
        }

        return (credentials.getEmailAddress().equals(context.getUserPrincipal().getName()));
    }
}