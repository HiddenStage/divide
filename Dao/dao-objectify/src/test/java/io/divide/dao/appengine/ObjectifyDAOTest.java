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

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.annotation.Entity;
import io.divide.dao.DAOTest;
import io.divide.dao.Keyable;
import io.divide.shared.transitory.TransientObject;
import org.junit.After;
import org.junit.Before;

import java.util.List;

import static io.divide.dao.appengine.OfyService.ofy;

public class ObjectifyDAOTest extends DAOTest<ObjectifyDAOTest.KeyedOfyObject> {

    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    public ObjectifyDAOTest(){
        super(new ObjectifyDAO());
        ObjectifyService.register(OfyObject.class);
    }

    @Override
    public KeyedOfyObject toBaseObject(TransientObject object) {
        return new KeyedOfyObject(object);
    }

    @Override
    public void rawSave(List<KeyedOfyObject> keyedOfyObjects) {
        for (KeyedOfyObject k : keyedOfyObjects){
            ofy().save().entities(k.getOfyObject()).now(); // queries are for OfyObject, have to save as one.
        }
    }

    @Before
    public void setUp() {
        helper.setUp();
        super.setUp();
    }

    @After
    public void tearDown() {
        helper.tearDown();
        super.tearDown();
    }

    @Entity
    public static class KeyedOfyObject extends OfyObject implements Keyable{

        public KeyedOfyObject(){
            super();
        }

        public KeyedOfyObject(TransientObject object){
            super(object.getObjectKey(),object.getUserData(),object.getMetaData());
        }

        @Override
        public String getKey() {
            return this.object_key;
        }

        public OfyObject getOfyObject(){
            return new OfyObject(object_key,user_data,meta_data);
        }
    }

}
