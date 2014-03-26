package io.divide.server;

import io.divide.dao.DAO;
import io.divide.dao.orientdb.OrientDBDao;

/**
 * Created by williamwebb on 2/20/14.
 */
public class TestApplication extends AuthApplication {

    public TestApplication(){
        super(OrientDBDao.class, TestUtils.KEY);
        register(TestEndpoint.class);
    }
    public TestApplication(DAO dao) {
        super(dao, TestUtils.KEY);
        register(TestEndpoint.class);
    }
}
