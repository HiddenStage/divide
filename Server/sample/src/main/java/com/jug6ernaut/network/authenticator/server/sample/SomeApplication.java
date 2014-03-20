package com.jug6ernaut.network.authenticator.server.sample;

import com.jug6ernaut.network.authenticator.server.AuthApplication;
import com.jug6ernaut.network.dao.orientdb.OrientDBDao;

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
