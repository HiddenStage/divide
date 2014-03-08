package com.jug6ernaut.network.authenticator.server;

import org.glassfish.jersey.server.ApplicationHandler;
import org.glassfish.jersey.test.spi.TestContainer;

import javax.ws.rs.core.Application;
import java.net.URI;

/**
 * Created by williamwebb on 2/27/14.
 */
public class AuthServerHelper {

    TestContainer container;

    public void setUp(final String url){
        URI uri = URI.create(url);
        Application a = new TestApplication();
        ApplicationHandler h = new ApplicationHandler(a);
        container = new WebContainerFactory().create(uri,h);
        container.start();
    }

    public void tearDown(){
        if(container != null)
        container.stop();
    }

}
