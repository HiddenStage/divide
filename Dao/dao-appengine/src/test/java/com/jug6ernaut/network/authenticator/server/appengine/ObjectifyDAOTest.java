package com.jug6ernaut.network.authenticator.server.appengine;

import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.annotation.Entity;
import com.jug6ernaut.network.dao.DAOTest;
import com.jug6ernaut.network.dao.Keyable;
import com.jug6ernaut.network.shared.web.transitory.TransientObject;
import org.junit.After;
import org.junit.Before;

import java.util.List;

import static com.jug6ernaut.network.authenticator.server.appengine.OfyService.ofy;

/**
 * Created by williamwebb on 11/16/13.
 */
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
