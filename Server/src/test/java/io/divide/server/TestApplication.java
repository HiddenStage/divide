package io.divide.server;

import io.divide.dao.ServerDAO;
import io.divide.dao.orientdb.OrientDBDao;

/**
 * Created by williamwebb on 2/20/14.
 */
public class TestApplication extends AuthApplication {

    public TestApplication(){
        super(OrientDBDao.class, TestUtils.KEY);
        register(TestEndpoint.class);
    }
    public TestApplication(ServerDAO serverDao) {
        super(serverDao, TestUtils.KEY);
        register(TestEndpoint.class);
    }
}
