package com.jug6ernaut.network.authenticator.server.sample;

import com.jug6ernaut.network.authenticator.server.AuthApplication;
import com.jug6ernaut.network.authenticator.server.appengine.ObjectifyDAO;
import com.jug6ernaut.network.authenticator.server.dao.DAOManager;
import com.jug6ernaut.network.shared.event.EventManager;
import com.jug6ernaut.network.shared.event.Subscriber;
import org.glassfish.hk2.api.ServiceLocator;

import javax.inject.Inject;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 8/19/13
 * Time: 5:57 PM
 */
public class SomeApplication extends AuthApplication<ObjectifyDAO> {

    Logger logger = Logger.getLogger(SomeApplication.class.getName());

    @Inject
    public SomeApplication(ServiceLocator serviceLocator) {
        super(serviceLocator);

        EventManager.get().register(
                new Subscriber<DAOManager.QUERY_EVENT>() {
                    @Override
                    public void onEvent(DAOManager.QUERY_EVENT event) {
                        logger.info("Query Event: " + event);
                    }
                });
        EventManager.get().register(
                new Subscriber<DAOManager.SAVE_EVENT>() {
                    @Override
                    public void onEvent(DAOManager.SAVE_EVENT event) {
                        logger.info("Send Event: " + event);
                    }
                });
    }

    @Override
    public Class<ObjectifyDAO> getDAO() {
        return ObjectifyDAO.class;
    }

}
