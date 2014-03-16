package com.jug6ernaut.network.authenticator.server;

import com.jug6ernaut.network.authenticator.server.auth.*;
import com.jug6ernaut.network.authenticator.server.dao.CredentialBodyHandler;
import com.jug6ernaut.network.authenticator.server.dao.DAOManager;
import com.jug6ernaut.network.authenticator.server.dao.GsonMessageBodyHandler;
import com.jug6ernaut.network.authenticator.server.dao.Session;
import com.jug6ernaut.network.authenticator.server.endpoints.AuthenticationEndpoint;
import com.jug6ernaut.network.authenticator.server.endpoints.DataEndpoint;
import com.jug6ernaut.network.authenticator.server.endpoints.MetaEndpoint;
import com.jug6ernaut.network.authenticator.server.endpoints.PushEndpoint;
import com.jug6ernaut.network.dao.DAO;
import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

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

    public AuthApplication(T t, String encryptionKey){
        this(new MyBinder<T>(t, encryptionKey));
    }

    public AuthApplication(Class<T> daoClass,String encryptionKey){
        this(new MyBinder<T>(daoClass,encryptionKey));
    }

    private AuthApplication(MyBinder binder){
        logger.finest("STARTING: " + getClass().getSimpleName());
        logger.info("DAO: " + binder.getDAOName());
        reg(AuthenticationEndpoint.class);
        reg(DataEndpoint.class);
        reg(PushEndpoint.class);
        reg(MetaEndpoint.class);
        reg(CredentialBodyHandler.class);  // insures passwords are not sent back
        reg(GsonMessageBodyHandler.class); // serialize all objects with GSON
        reg(SecurityFilter.class);
        reg(ResponseFilter.class);
//        reg(GZIPReaderInterceptor.class);

        register(binder);

        property("jersey.config.workers.legacyOrdering", true);
    }


    private static class MyBinder<T extends DAO> extends AbstractBinder{
        private Class<T> clazz;
        private T t;
        private String encryptionKey;

        public MyBinder(T dao, String encryptionKey){
            this.t = dao;
            this.encryptionKey = encryptionKey;
        }

        public MyBinder(Class<T> daoClass, String encryptionKey){
            clazz = daoClass;
            this.encryptionKey = encryptionKey;
        }

        @Override
        protected void configure() {
            try {
                System.out.println("Class: " + clazz + " : " + t);
                if(t == null)
                    t = clazz.newInstance();
                DAOManager manager = new DAOManager(t);
                bind(manager).to(DAOManager.class);
                bind(new SecManager(manager,encryptionKey)).to(SecManager.class);
                bind(UserContext.class).to(SecurityContext.class);
                bind(Session.class).to(Session.class);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

        public String getDAOName(){
            if(t != null)
                return t.getClass().getSimpleName();
            else
                return clazz.getSimpleName();
        }
    }

    private void reg(Class<?> clazz){
        //logger.info("Registering: " + clazz.getSimpleName());
        this.register(clazz);
    }

    private void isReg(Object o){
        logger.info("isRegistered("+o.getClass().getSimpleName()+"): " + isRegistered(o));
    }

    private void isReg(Class<?> clazz){
        logger.info("isRegistered("+clazz.getSimpleName()+"): " + isRegistered(clazz));
    }

}
