package io.divide.shared.server;

import io.divide.shared.web.transitory.TransientObject;

/**
 * Created by williamwebb on 4/11/14.
 */
public abstract class ServerLogic<DAOOut extends TransientObject> {

    protected DAO<TransientObject,DAOOut> dao;

    public ServerLogic(DAO<TransientObject,DAOOut> dao){
        this.dao = dao;
    }

}
