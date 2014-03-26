package io.divide.server.sample;

import io.divide.server.AuthApplication;
import io.divide.dao.orientdb.OrientDBDao;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 8/19/13
 * Time: 5:57 PM
 */
public class SomeApplication extends AuthApplication<OrientDBDao> {

    public SomeApplication() {
        super(OrientDBDao.class, "saywhatwhat");
    }

}
