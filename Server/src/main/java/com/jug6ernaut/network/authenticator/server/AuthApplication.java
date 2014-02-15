package com.jug6ernaut.network.authenticator.server;

import com.jug6ernaut.network.authenticator.server.auth.KeyManager;
import com.jug6ernaut.network.authenticator.server.auth.ResponseFilter;
import com.jug6ernaut.network.authenticator.server.auth.SecurityFilter;
import com.jug6ernaut.network.authenticator.server.auth.UserContext;
import com.jug6ernaut.network.authenticator.server.dao.CredentialBodyHandler;
import com.jug6ernaut.network.authenticator.server.dao.DAOManager;
import com.jug6ernaut.network.authenticator.server.dao.GsonMessageBodyHandler;
import com.jug6ernaut.network.authenticator.server.dao.Session;
import com.jug6ernaut.network.authenticator.server.endpoints.AuthenticationEndpoint;
import com.jug6ernaut.network.authenticator.server.endpoints.DataEndpoint;
import com.jug6ernaut.network.authenticator.server.endpoints.MetaEndpoint;
import com.jug6ernaut.network.authenticator.server.endpoints.PushEndpoint;
import com.jug6ernaut.network.dao.DAO;
import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.internal.inject.Injections;
import org.glassfish.jersey.process.internal.RequestScoped;
import org.glassfish.jersey.server.ResourceConfig;

import javax.inject.Inject;
import javax.ws.rs.core.SecurityContext;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 8/19/13
 * Time: 5:39 PM
 */
public abstract class AuthApplication<T extends DAO> extends ResourceConfig {

    private static final Logger logger = Logger.getLogger(AuthApplication.class.getSimpleName());

    @Inject
    public AuthApplication(ServiceLocator serviceLocator){

        reg(AuthenticationEndpoint.class);
        reg(DataEndpoint.class);
        reg(PushEndpoint.class);
        reg(MetaEndpoint.class);
        reg(CredentialBodyHandler.class);  // insures passwords are not sent back
        reg(GsonMessageBodyHandler.class); // serialize all objects with GSON
        reg(SecurityFilter.class);
        reg(ResponseFilter.class);
        reg(GZIPReaderInterceptor.class);

        DynamicConfiguration dc = Injections.getConfiguration(serviceLocator);
        bind(dc,getDAO(),"somekey");

        property("jersey.config.workers.legacyOrdering",true);
    }

    public abstract Class<T> getDAO();

    private void reg(Class<?> clazz){
        //logger.info("Registering: " + clazz.getSimpleName());
        this.register(clazz);
    }

    public void bind(DynamicConfiguration dc, Class<T> daoClass, String encryptionKey){
        try {
            T t = (T) Class.forName(daoClass.getName()).newInstance();
            DAOManager manager = new DAOManager(t);
            Injections.addBinding(
                    Injections.newBinder(manager).to(DAOManager.class),
                    dc);
            Injections.addBinding(
                    Injections.newBinder(new KeyManager(manager,encryptionKey)).to(KeyManager.class),
                    dc);
        } catch (Exception e) {
            logger.severe("Failed to register DAO");
        }
        try {
            Injections.addBinding(
                    Injections.newBinder(UserContext.class).to(SecurityContext.class).in(RequestScoped.class),
                    dc);
        } catch (Exception e) {
            logger.severe("Failed to register UserContext");
        }
        try {
            Injections.addBinding(
                    Injections.newBinder(Session.class).to(Session.class),
                    dc);
        } catch (Exception e) {
            logger.severe("Failed to register UserContext");
        }

        // commits changes
        dc.commit();
    }

    private void isReg(Object o){
        logger.info("isRegistered("+o.getClass().getSimpleName()+"): " + isRegistered(o));
    }

    private void isReg(Class<?> clazz){
        logger.info("isRegistered("+clazz.getSimpleName()+"): " + isRegistered(clazz));
    }

}
