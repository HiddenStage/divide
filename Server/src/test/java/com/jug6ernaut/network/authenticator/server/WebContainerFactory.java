package com.jug6ernaut.network.authenticator.server;


import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.grizzly2.servlet.GrizzlyWebContainerFactory;
import org.glassfish.jersey.server.ApplicationHandler;
import org.glassfish.jersey.test.spi.TestContainer;
import org.glassfish.jersey.test.spi.TestContainerException;
import org.glassfish.jersey.test.spi.TestContainerFactory;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by williamwebb on 2/20/14.
 */
public class WebContainerFactory implements TestContainerFactory {

    private static class MyTestContainer implements TestContainer {
        private final URI uri;
        private final ApplicationHandler appHandler;
        private org.glassfish.grizzly.http.server.HttpServer server;
        private static final Logger LOGGER = Logger.getLogger(WebContainerFactory.class.getName());

        private MyTestContainer(URI uri, ApplicationHandler appHandler) {
            this.appHandler = appHandler;
            this.uri = uri;
            System.out.println(uri);
        }

        @Override
        public ClientConfig getClientConfig() {
            return null;
        }

        @Override
        public URI getBaseUri() {
            return uri;
        }

        @Override
        public void start() {
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, "Starting GrizzlyTestContainer...");
            }

            try {
                this.server = GrizzlyWebContainerFactory.create(uri,
                        Collections.singletonMap("javax.ws.rs.Application",
                        appHandler.getConfiguration().getApplication().getClass().getName()));

            } catch (ProcessingException e) {
                throw new TestContainerException(e);
            } catch (IOException e) {
                throw new TestContainerException(e);
            }
        }

        @Override
        public void stop() {
            if (LOGGER.isLoggable(Level.INFO)) {
                LOGGER.log(Level.INFO, "Stopping GrizzlyTestContainer...");
            }
            this.server.shutdownNow();

        }
    }

    private static int count = 0;
    private static synchronized int getCount(){
        if(incrementPort)
            return count++;
        else
            return count;
    }
    private static boolean incrementPort = true;

    public void enableEncrementPort(boolean incrementPort){
        WebContainerFactory.incrementPort = incrementPort;
    }


    @Override
    public TestContainer create(URI baseUri, ApplicationHandler application) throws IllegalArgumentException {
        URI uri = UriBuilder.fromUri(baseUri).port(baseUri.getPort() + getCount()).build();
        System.out.println("Uri: " + uri);
        System.out.println("App: " + application.getConfiguration().getApplication().getClass().getName());

        return new MyTestContainer(uri, application);
    }
}