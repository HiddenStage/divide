package com.jug6ernaut.network.authenticator.server.sample;

import com.jug6ernaut.network.authenticator.server.AuthApplication;
import com.jug6ernaut.network.authenticator.server.appengine.ObjectifyDAO;

import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 8/19/13
 * Time: 5:57 PM
 */
public class SomeApplication extends AuthApplication<ObjectifyDAO> {

    Logger logger = Logger.getLogger(SomeApplication.class.getName());

    public SomeApplication() {
        super(ObjectifyDAO.class, "saywhatwhat");

//        EventManager.get().register(
//                new Subscriber<DAOManager.QUERY_EVENT>() {
//                    @Override
//                    public void onEvent(DAOManager.QUERY_EVENT event) {
//                        logger.info("Query Event: " + event);
//                    }
//                });
//        EventManager.get().register(
//                new Subscriber<DAOManager.SAVE_EVENT>() {
//                    @Override
//                    public void onEvent(DAOManager.SAVE_EVENT event) {
//                        logger.info("Send Event: " + event);
//                    }
//                });
    }

}
