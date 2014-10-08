/*
 * Copyright (C) 2014 Divide.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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