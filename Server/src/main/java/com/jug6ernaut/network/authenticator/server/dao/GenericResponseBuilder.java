package com.jug6ernaut.network.authenticator.server.dao;

import javax.ws.rs.core.*;
import javax.ws.rs.core.Link.Builder;
import java.lang.annotation.Annotation;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.*;

;import static javax.ws.rs.core.Response.Status;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 9/2/13
 * Time: 9:29 PM
 */


public class GenericResponseBuilder<T>
{
        protected T entity;
        protected Annotation[] entityAnnotations;
        protected Status status = null;
        protected MultivaluedMap<String,Object> metadata = new MultivaluedHashMap<String,Object>();

        public GenericResponse<T> build()
        {
            if (status == null && entity == null) status = Status.OK;
            else if (status == null) status = Status.OK;
            return new GenericResponse<T>(
                    Response
                            .status(status)
                            .replaceAll(metadata)
                            .entity(entity)
                            .build()
            );
        }

        public GenericResponseBuilder clone()
        {
            GenericResponseBuilder impl = new GenericResponseBuilder();
            impl.metadata.putAll(metadata);
            impl.entity = entity;
            impl.status = status;
            impl.entityAnnotations = entityAnnotations;
            return impl;
        }

        public GenericResponseBuilder status(Status status)
        {
            this.status = status;
            return this;
        }

        public GenericResponseBuilder entity(T entity)
        {
            this.entity = entity;
            return this;
        }

        public GenericResponseBuilder entity(T entity, Annotation[] annotations)
        {
            this.entity = entity;
            this.entityAnnotations = annotations;
            return this;
        }

        public GenericResponseBuilder type(MediaType type)
        {
            if (type == null)
            {
                metadata.remove(HttpHeaders.CONTENT_TYPE);
                return this;
            }
            metadata.putSingle(HttpHeaders.CONTENT_TYPE, type);
            return this;
        }

        public GenericResponseBuilder type(String type)
        {
            if (type == null)
            {
                metadata.remove(HttpHeaders.CONTENT_TYPE);
                return this;
            }
            metadata.putSingle(HttpHeaders.CONTENT_TYPE, type);
            return this;
        }

        public GenericResponseBuilder variant(Variant variant)
        {
            if (variant == null)
            {
                type((String)null);
                language((String)null);
                metadata.remove(HttpHeaders.CONTENT_ENCODING);
                return this;
            }
            type(variant.getMediaType());
            language(variant.getLanguage());
            if (variant.getEncoding() != null) metadata.putSingle(HttpHeaders.CONTENT_ENCODING, variant.getEncoding());
            else metadata.remove(HttpHeaders.CONTENT_ENCODING);
            return this;
        }

        public GenericResponseBuilder variants(List<Variant> variants)
        {
            if (variants == null)
            {
                metadata.remove(HttpHeaders.VARY);
                return this;
            }
            String vary = createVaryHeader(variants);
            metadata.putSingle(HttpHeaders.VARY, vary);

            return this;
        }

        public static String createVaryHeader(List<Variant> variants)
        {
            boolean accept = false;
            boolean acceptLanguage = false;
            boolean acceptEncoding = false;

            for (Variant variant : variants)
            {
                if (variant.getMediaType() != null) accept = true;
                if (variant.getLanguage() != null) acceptLanguage = true;
                if (variant.getEncoding() != null) acceptEncoding = true;
            }

            String vary = null;
            if (accept) vary = HttpHeaders.ACCEPT;
            if (acceptLanguage)
            {
                if (vary == null) vary = HttpHeaders.ACCEPT_LANGUAGE;
                else vary += ", " + HttpHeaders.ACCEPT_LANGUAGE;
            }
            if (acceptEncoding)
            {
                if (vary == null) vary = HttpHeaders.ACCEPT_ENCODING;
                else vary += ", " + HttpHeaders.ACCEPT_ENCODING;
            }
            return vary;
        }


        public GenericResponseBuilder language(String language)
        {
            if (language == null)
            {
                metadata.remove(HttpHeaders.CONTENT_LANGUAGE);
                return this;
            }
            metadata.putSingle(HttpHeaders.CONTENT_LANGUAGE, language);
            return this;
        }


//        public GenericResponseBuilder location(URI location)
//        {
//
//            if (location == null)
//            {
//                metadata.remove(HttpHeaders.LOCATION);
//                return this;
//            }
//            if (!location.isAbsolute() && ResteasyProviderFactory.getContextData(HttpRequest.class) != null)
//            {
//                String path = location.toString();
//                if (path.startsWith("/")) path = path.substring(1);
//                URI baseUri = ResteasyProviderFactory.getContextData(HttpRequest.class).getUri().getBaseUri();
//                location = baseUri.resolve(path);
//            }
//            metadata.putSingle(HttpHeaders.LOCATION, location);
//            return this;
//        }
//
//
//        public GenericResponseBuilder contentLocation(URI location)
//        {
//            if (location == null)
//            {
//                metadata.remove(HttpHeaders.CONTENT_LOCATION);
//                return this;
//            }
//            if (!location.isAbsolute() && ResteasyProviderFactory.getContextData(HttpRequest.class) != null)
//            {
//                String path = location.toString();
//                if (path.startsWith("/")) path = path.substring(1);
//                URI baseUri = ResteasyProviderFactory.getContextData(HttpRequest.class).getUri().getBaseUri();
//                location = baseUri.resolve(path);
//            }
//            metadata.putSingle(HttpHeaders.CONTENT_LOCATION, location);
//            return this;
//        }


        public GenericResponseBuilder tag(EntityTag tag)
        {
            if (tag == null)
            {
                metadata.remove(HttpHeaders.ETAG);
                return this;
            }
            metadata.putSingle(HttpHeaders.ETAG, tag);
            return this;
        }


        public GenericResponseBuilder tag(String tag)
        {
            if (tag == null)
            {
                metadata.remove(HttpHeaders.ETAG);
                return this;
            }
            metadata.putSingle(HttpHeaders.ETAG, tag);
            return this;
        }


        public GenericResponseBuilder lastModified(Date lastModified)
        {
            if (lastModified == null)
            {
                metadata.remove(HttpHeaders.LAST_MODIFIED);
                return this;
            }
            metadata.putSingle(HttpHeaders.LAST_MODIFIED, lastModified);
            return this;
        }


        public GenericResponseBuilder cacheControl(CacheControl cacheControl)
        {
            if (cacheControl == null)
            {
                metadata.remove(HttpHeaders.CACHE_CONTROL);
                return this;
            }
            metadata.putSingle(HttpHeaders.CACHE_CONTROL, cacheControl);
            return this;
        }


        public GenericResponseBuilder header(String name, Object value)
        {
            if (value == null)
            {
                metadata.remove(name);
                return this;
            }
            metadata.add(name, value);
            return this;
        }


        public GenericResponseBuilder cookie(NewCookie... cookies)
        {
            if (cookies == null)
            {
                metadata.remove(HttpHeaders.SET_COOKIE);
                return this;
            }
            for (NewCookie cookie : cookies)
            {
                metadata.add(HttpHeaders.SET_COOKIE, cookie);
            }
            return this;
        }

        public GenericResponseBuilder language(Locale language)
        {
            if (language == null)
            {
                metadata.remove(HttpHeaders.CONTENT_LANGUAGE);
                return this;
            }
            metadata.putSingle(HttpHeaders.CONTENT_LANGUAGE, language);
            return this;
        }

        public static SimpleDateFormat getDateFormatRFC822()
        {
            SimpleDateFormat dateFormatRFC822 = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
            dateFormatRFC822.setTimeZone(TimeZone.getTimeZone("GMT"));
            return dateFormatRFC822;
        }

        public GenericResponseBuilder expires(Date expires)
        {
            if (expires == null)
            {
                metadata.remove(HttpHeaders.EXPIRES);
                return this;
            }
            metadata.putSingle(HttpHeaders.EXPIRES, getDateFormatRFC822().format(expires));
            return this;
        }

        // spec


//        public GenericResponseBuilder allow(String... methods)
//        {
//            if (methods == null)
//            {
//                return allow((Set<String>)null);
//            }
//            HashSet<String> set = new HashSet<String>();
//            for (String m : methods) set.add(m);
//            return allow(set);
//        }

//        public GenericResponseBuilder allow(Set<String> methods)
//        {
//            HeaderHelper.setAllow(this.metadata, methods);
//            return this;
//        }

        public GenericResponseBuilder encoding(String encoding)
        {
            if (encoding == null)
            {
                metadata.remove(HttpHeaders.CONTENT_ENCODING);
                return this;
            }
            metadata.putSingle(HttpHeaders.CONTENT_ENCODING, encoding);
            return this;
        }


        public GenericResponseBuilder variants(Variant... variants)
        {
            return this.variants(Arrays.asList(variants));
        }


        public GenericResponseBuilder links(Link... links)
        {
            metadata.remove(HttpHeaders.LINK);
            for (Link link : links)
            {
                metadata.add(HttpHeaders.LINK, link);
            }
            return this;
        }


        public GenericResponseBuilder link(URI uri, String rel)
        {
            Link link = Link.fromUri(uri).rel(rel).build();
            metadata.add(HttpHeaders.LINK, link);
            return this;
        }


        public GenericResponseBuilder link(String uri, String rel)
        {
            Link link = Link.fromUri(uri).rel(rel).build();
            metadata.add(HttpHeaders.LINK, link);
            return this;
        }


        public GenericResponseBuilder replaceAll(MultivaluedMap<String, Object> headers)
        {
            metadata.clear();
            if (headers == null) return this;
            metadata.putAll(headers);
            return this;
        }

        public static class GenericResponse<T> extends Response implements AutoCloseable {

        final Response delegate;

        GenericResponse(Response response) {
            this.delegate = response;
        }

        public int hashCode() {
            return delegate.hashCode();
        }

        public int getStatus() {
            return delegate.getStatus();
        }

        public StatusType getStatusInfo() {
            return delegate.getStatusInfo();
        }

        public boolean equals(Object obj) {
            return delegate.equals(obj);
        }

        public Object getEntity() {
            return delegate.getEntity();
        }

        public <T> T readEntity(Class<T> entityType) {
            return delegate.readEntity(entityType);
        }

        public <T> T readEntity(GenericType<T> entityType) {
            return delegate.readEntity(entityType);
        }

        public <T> T readEntity(Class<T> entityType, Annotation[] annotations) {
            return delegate.readEntity(entityType, annotations);
        }

        public String toString() {
            return delegate.toString();
        }

        public <T> T readEntity(GenericType<T> entityType, Annotation[] annotations) {
            return delegate.readEntity(entityType, annotations);
        }

        public boolean hasEntity() {
            return delegate.hasEntity();
        }

        public boolean bufferEntity() {
            return delegate.bufferEntity();
        }

        public MediaType getMediaType() {
            return delegate.getMediaType();
        }

        public Locale getLanguage() {
            return delegate.getLanguage();
        }

        public int getLength() {
            return delegate.getLength();
        }

        public Set<String> getAllowedMethods() {
            return delegate.getAllowedMethods();
        }

        public Map<String, NewCookie> getCookies() {
            return delegate.getCookies();
        }

        public EntityTag getEntityTag() {
            return delegate.getEntityTag();
        }

        public Date getDate() {
            return delegate.getDate();
        }

        public Date getLastModified() {
            return delegate.getLastModified();
        }

        public URI getLocation() {
            return delegate.getLocation();
        }

        public Set<Link> getLinks() {
            return delegate.getLinks();
        }

        public boolean hasLink(String relation) {
            return delegate.hasLink(relation);
        }

        public Link getLink(String relation) {
            return delegate.getLink(relation);
        }

        public Builder getLinkBuilder(String relation) {
            return delegate.getLinkBuilder(relation);
        }

        public MultivaluedMap<String, Object> getMetadata() {
            return delegate.getMetadata();
        }

        public MultivaluedMap<String, Object> getHeaders() {
            return delegate.getHeaders();
        }

        public MultivaluedMap<String, String> getStringHeaders() {
            return delegate.getStringHeaders();
        }

        public String getHeaderString(String name) {
            return delegate.getHeaderString(name);
        }

        @Override
        public void close() {
            delegate.close();
        }

        public static GenericResponse of(Response response) {
            return new GenericResponse(response);
        }
    }
}
