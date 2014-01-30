package com.jug6ernaut.network.authenticator.server.appengine;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.LoadType;
import com.jug6ernaut.network.authenticator.server.dao.DAO;
import com.jug6ernaut.network.shared.util.Crypto;
import com.jug6ernaut.network.shared.util.ObjectUtils;
import com.jug6ernaut.network.shared.web.transitory.TransientObject;
import com.jug6ernaut.network.shared.web.transitory.query.Clause;
import com.jug6ernaut.network.shared.web.transitory.query.Query;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.ws.rs.core.Response;
import java.security.KeyPair;
import java.util.*;
import java.util.logging.Logger;

import static com.jug6ernaut.network.authenticator.server.appengine.OfyService.ofy;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 8/5/13
 * Time: 4:50 PM
 */

public class ObjectifyDAO implements DAO {
    Logger logger = Logger.getLogger(String.valueOf(ObjectifyDAO.class));
    Random RANDOM = new Random();

    @Override
    public List<TransientObject> query(Query query) throws DAOException{
        logger.info("query: " + query);
        LoadType<?> filter = ofy().load().type(OfyObject.class);
        com.googlecode.objectify.cmd.Query<?> oFilter =
        filter.filter(TransientObject.META_DATA+"."+ TransientObject.OBJECT_TYPE_KEY.KEY + " =",query.getFrom());

        for(Clause c : (Collection<Clause>)query.getWhere().values()){
            oFilter = oFilter.filter(c.getBefore() + " " + c.getOperand(), c.getAfter());
        }

        if(query.getOffset()!=null){
            oFilter = oFilter.offset(query.getOffset());
        }
        if(query.getLimit()!=null){
            oFilter = oFilter.limit(query.getLimit());
        }
        if(query.getRandom()!=null){
            int count = count(query.getFrom());
            if(count < 1) return new ArrayList<TransientObject>();

            int random = RANDOM.nextInt(count);
            oFilter = oFilter.offset(random);
            oFilter = oFilter.limit(query.getLimit());
        }

        List list = null;
        switch (query.getAction()){
            case SELECT:{
                list = oFilter.list();
                List<TransientObject> toReturn = new ArrayList<TransientObject>(list.size());
                try{
                    for (OfyObject oo : (List<OfyObject>)list){
                        logger.info("Got: " + oo);
                        toReturn.add(BackendToOfy.getBack(oo));
                    }
                } catch (Exception e) {
                    throw new DAOException(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),e);
                }

                list.clear();
                list = toReturn;
            }break;
            case DELETE:{
                list = oFilter.keys().list();
                ofy().delete().keys(list);
            }break;
            case UPDATE:{
                throw new NotImplementedException();
            }
        }

        logger.info("Query Complete: " + list);

        return (List<TransientObject>) list;
    }

    @Override
    public Collection<TransientObject> get(final String... keys) throws DAOException {
        logger.info("get: " + ObjectUtils.v2c(keys));

        List<Key<OfyObject>> ofyKeys = new ArrayList<Key<OfyObject>>(keys.length);
        for (String to : keys){
            ofyKeys.add(Key.create(OfyObject.class, to));
        }

        Map<Key<OfyObject>, OfyObject> ofyObjets = ofy().load().keys(ofyKeys);

        List<TransientObject> tos = new ArrayList<TransientObject>(ofyObjets.size());
        try{
            for (OfyObject oo : ofyObjets.values()){
                tos.add(BackendToOfy.getBack(oo));
            }
        } catch (Exception e) {
            throw new DAOException(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),e);
        }

        logger.info("Get Complete: " + tos);
        return tos;
    }

    @Override
    public void save(TransientObject... objects) throws DAOException{
        logger.info("save(): " + ObjectUtils.v2c(objects));
        try{
            for(TransientObject bo : objects){
                ofy().save().entities(BackendToOfy.getOfy(bo)).now();
            }
        } catch (Exception e) {
            throw new DAOException(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),e);
        }
        logger.info("Save Complete.");
    }

    @Override
    public void delete(TransientObject... objects) throws DAOException {
        logger.info("delete: " + ObjectUtils.v2c(objects));

        try{
            for(TransientObject bo : objects){
                ofy().delete().key(Key.create(OfyObject.class, bo.getObjectKey())).now();
            }
        } catch (Exception e) {
            throw new DAOException(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),e);
        }
        logger.info("Delete Complete.");
    }

    @Override
    public boolean exists(TransientObject... objects) {
        logger.info("exists: " + ObjectUtils.v2c(objects));

        boolean exists = true;
        for(TransientObject bo : objects){
            if(!exists(bo.getObjectKey())) exists = false;
        }
        return exists;
    }

    @Override
    public int count(String objectType) {
        com.googlecode.objectify.cmd.Query<?> query =
                ofy().
                load().
                type(OfyObject.class).
                filter(TransientObject.META_DATA + "." + TransientObject.OBJECT_TYPE_KEY + " =", objectType);
        int count = query.count();

        logger.info(TransientObject.META_DATA + "." + TransientObject.OBJECT_TYPE_KEY + " =" + objectType + ": " + count);

        return query.count();
    }

    @Override
    public KeyPair keys(KeyPair keys) {
        if(keys!=null){
            KeyObject keyObject = new KeyObject();
            keyObject.setKeys(keys.getPublic().getEncoded(), keys.getPrivate().getEncoded()); 
            ofy().save().entities(keyObject);
            return keys;
        } else {
            List<KeyObject> keyObject = ofy().load().type(KeyObject.class).limit(1).list();
            KeyObject key = ObjectUtils.get1stOrNull(keyObject);
            if(key!=null){
                return Crypto.createKeyPair(key.getPublicKey(),key.getPrivateKey());
            }else
                return null;
        }
    }

    private boolean exists(String key){
        return (OfyService.ofy().load().filterKey(Key.create(OfyObject.class,key)).count() == 1);
    }
}
