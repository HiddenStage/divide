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

package io.divide.server;

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