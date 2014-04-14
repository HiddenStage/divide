package io.divide.dao;

import io.divide.shared.server.DAO;
import io.divide.shared.web.transitory.TransientObject;

import java.security.KeyPair;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 8/5/13
 * Time: 4:39 PM
 */

public interface ServerDAO extends DAO<TransientObject,TransientObject> {
    public KeyPair keys(KeyPair keys);
}
