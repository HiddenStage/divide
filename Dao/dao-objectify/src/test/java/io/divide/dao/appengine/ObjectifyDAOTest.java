/*
 * Copyright (C) 2014 Divide.io
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
