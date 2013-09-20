package com.jug6ernaut.network.authenticator.server.sample;

import com.jug6ernaut.network.authenticator.server.AuthApplication;
import com.jug6ernaut.network.authenticator.server.appengine.ObjectifyDAO;
import com.jug6ernaut.network.authenticator.server.dao.EventNotifier;
import com.jug6ernaut.network.shared.util.ObjectUtils;
import com.jug6ernaut.network.shared.web.transitory.Credentials;
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

        addEventNotifier(new EventNotifier.Notifier<Credentials>(Credentials.class) {
            @Override
            public void onEvent(Credentials... object) {
                logger.info("onEvent: " + ObjectUtils.v2c(object));
            }

            @Override
            public boolean condition(String action, Credentials object) {
                return (object.getEmailAddress().equals("aaa"));
            }
        });
    }

    @Override
    public Class<ObjectifyDAO> getDAO() {
        return ObjectifyDAO.class;
    }

}
