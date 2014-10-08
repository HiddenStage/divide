/*
 * Copyright (C) 2014 Divide.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.divide.dao.appengine;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.LoadType;
import io.divide.dao.ServerDAO;
import io.divide.shared.util.Crypto;
import io.divide.shared.util.ObjectUtils;
import io.divide.shared.transitory.TransientObject;
import io.divide.shared.transitory.query.*;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.ws.rs.core.Response;
import java.security.KeyPair;
import java.util.*;
import java.util.logging.Logger;

import static io.divide.dao.appengine.OfyService.ofy;

public class ObjectifyDAO implements ServerDAO {
    Logger logger = Logger.getLogger(String.valueOf(ObjectifyDAO.class));
    Random RANDOM = new Random();

    @Override
    public List<TransientObject> query(Query query) throws DAOException{
        logger.info("query: " + query);
        LoadType<?> filter = ofy().load().type(OfyObject.class);
        com.googlecode.objectify.cmd.Query<?> oFilter =
        filter.filter(TransientObject.META_DATA+"."+ TransientObject.OBJECT_TYPE_KEY.KEY + " =",query.getFrom());

        for(Clause c : query.getWhere().values()){
            oFilter = oFilter.filter(
                    c.getBefore() + " " +
                    (c.getOperand().equals(OPERAND.CONTAINS.toString())?OPERAND.EQ.toString():c.getOperand()), // replace CONTAINS with ==
                    c.getAfter());
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
                if(query.getSelect() == null){
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
                } else
                if(query.getSelect().equals(SelectOperation.COUNT)){
                    int count = count(query.getFrom());
                    return Arrays.asList((TransientObject)new Count(count,query.getFrom()));
                }
            }break;
            case DELETE:{
                list = oFilter.keys().list();
                ofy().delete().keys(list);
                int count = list.size();
                list.clear();
                EmptyTO o = new EmptyTO();
                o.put("count",count);
                list.add(o);
            }break;
            case UPDATE:{
                throw new NotImplementedException();
            }
        }

        logger.info("Query Complete: " + list);

        return (List<TransientObject>) list;
    }

    @Override
    public Collection<TransientObject> get(String objectType, final String... keys) throws DAOException {
        logger.info("get: " + ObjectUtils.v2c(keys));

        List<Key<OfyObject>> ofyKeys = new ArrayList<Key<OfyObject>>(keys.length);
        for (String to : keys){
            ofyKeys.add(Key.create(OfyObject.class, to));
        }

        Map<Key<OfyObject>, OfyObject> ofyObjets = ofy().load().keys(ofyKeys);

        List<TransientObject> tos = new ArrayList<TransientObject>(ofyObjets.size());
        try{
            for (OfyObject oo : ofyObjets.values()){
                if(objectType.equals( oo.meta_data.get("object_type") ))
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

    private static class EmptyTO extends TransientObject{

        protected EmptyTO() {
            super(TransientObject.class);
            this.meta_data.clear();
            this.user_data.clear();
        }

        @Override
        public void put(String key, Object value){
            super.put(key,value);
            meta_data.clear();
        }
    }
}
