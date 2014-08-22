/*
 * Copyright (C) 2014 Divide.io
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package io.divide.server.dao;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.divide.shared.transitory.Credentials;

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
                object = (T) object.getSafe();
            }

            getGson().toJson(object, jsonType, writer);
            logger.info("sending: " + object);

        } finally {
            writer.close();
        }
    }

    private static final boolean match(Credentials credentials, SecurityContext context){
        if(credentials == null){ logger.warning("credentials null"); return false; }
        if(credentials.getEmailAddress() == null){ logger.warning("credentials.getEmailAddress() null"); return false; }
        if(context == null){ logger.warning("context null"); return false; }
        if(context.getUserPrincipal() == null){ logger.warning("context.getUserPrincipal() null"); return false; }
        if(context.getUserPrincipal().getName() == null){ logger.warning("context.getUserPrincipal().getName() null"); return false; }

        logger.info("Logged In: " + credentials.getEmailAddress() + "\n" +
                    "Returning: " + context.getUserPrincipal().getName());

        return (credentials.getEmailAddress().equals(context.getUserPrincipal().getName()));
    }
}