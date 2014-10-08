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

//package io.divide.client.debug.push;
//
//import com.google.gson.Gson;
//import com.google.inject.Inject;
//import io.divide.client.auth.AuthManager;
//import io.divide.client.push.PushWebService;
//import io.divide.shared.server.DAO;
//import io.divide.shared.server.KeyManager;
//import io.divide.shared.web.transitory.Credentials;
//import io.divide.shared.web.transitory.EncryptedEntity;
//import io.divide.shared.web.transitory.TransientObject;
//import retrofit.Callback;
//import retrofit.http.Body;
//
//import static io.divide.shared.server.DAO.DAOException;
//import static io.divide.shared.web.transitory.EncryptedEntity.Reader;
//
//public class MockPushWebService implements PushWebService {
//
//    @Inject private DAO<TransientObject,TransientObject> dao;
//    @Inject private AuthManager authManager;
//    @Inject private KeyManager keyManager;
//
//    @Override
//    public Boolean register(@Body EncryptedEntity ent) {
//        try{
//            Reader entity = convert(Reader.class, ent);
//
//            Credentials credentials = authManager.getUser();
//            entity.setKey(keyManager.getPrivateKey());
//
//            credentials.setPushMessagingKey(entity.get("token"));
//            dao.save(credentials);
//            return true;
//        } catch (DAOException e) {
//            return false;
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//    @Override
//    public void register(@Body EncryptedEntity ent, Callback<Boolean> callback) {
//        try{
//            Reader entity = convert(Reader.class, ent);
//
//            Credentials credentials = authManager.getUser();
//            entity.setKey(keyManager.getPrivateKey());
//
//            credentials.setPushMessagingKey(entity.get("token"));
//            dao.save(credentials);
//            callback.success(true,null);
//        } catch (DAOException e) {
//            callback.success(false,null);
//        } catch (Exception e) {
//            callback.success(false,null);
//        }
//    }
//
//    @Override
//    public Boolean unregister() {
//        try{
//            Credentials credentials = authManager.getUser();
//            credentials.setPushMessagingKey("");
//            dao.save(credentials);
//            return true;
//        } catch (DAOException e) {
//            return false;
//        }
//    }
//
//    @Override
//    public void unregister(Callback<Boolean> callback) {
//        try{
//            Credentials credentials = authManager.getUser();
//            credentials.setPushMessagingKey("");
//            dao.save(credentials);
//            callback.success(true,null);
//        } catch (DAOException e) {
//            callback.failure(null);
//        }
//    }
//
//    private static Gson converter = new Gson();
//    private static <X, T extends X> T convert(Class<T> type, X from){
//        return converter.fromJson(converter.toJson(from),type);
//    }
//}
