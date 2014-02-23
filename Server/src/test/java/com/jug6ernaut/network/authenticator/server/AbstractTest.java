package com.jug6ernaut.network.authenticator.server;

import com.jug6ernaut.network.dao.DAO;
import org.glassfish.jersey.test.JerseyTest;
import org.glassfish.jersey.test.TestProperties;
import org.glassfish.jersey.test.spi.TestContainerFactory;
import org.junit.After;

import javax.ws.rs.core.Application;

/**
 * Created by williamwebb on 2/20/14.
 */
public abstract class AbstractTest extends JerseyTest {
    protected TestUtils.TestWrapper container;

    @Override
    protected TestContainerFactory getTestContainerFactory() {
        return new WebContainerFactory();
    }

    @Override
    protected Application configure() {
        container = TestUtils.setUp();
        enable(TestProperties.LOG_TRAFFIC);
        enable(TestProperties.DUMP_ENTITY);
        return container.app;
    }

    @After
    public void tearDown() throws DAO.DAOException {
        container.tearDown();
    }
}
