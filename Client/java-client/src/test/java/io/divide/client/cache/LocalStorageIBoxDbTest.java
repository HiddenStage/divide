package io.divide.client.cache;


import iBoxDB.LocalServer.BoxSystem;
import io.divide.dao.DAOTest;
import io.divide.dao.Keyable;
import io.divide.shared.transitory.TransientObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;

import java.util.List;

public class LocalStorageIBoxDbTest extends DAOTest<LocalStorageIBoxDbTest.KeyedWrapper> {

    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    public LocalStorageIBoxDbTest() {
        super(null);
    }

    @Before
    public void setUp() {
        dao = new LocalStorageIBoxDb(folder.getRoot().getPath());
        super.setUp();
    }

    @After
    public void tearDown() {
        if (!BoxSystem.DBDebug.DeleteDBFiles(1, 10, 20, -10)) {
            System.out.println("delete=false,system locks");
        }
    }

    @Override
    public KeyedWrapper toBaseObject(TransientObject object) {
        return new KeyedWrapper(object);
    }

    @Override
    public void rawSave(List<KeyedWrapper> keyedWrappers) {
        for(KeyedWrapper kw : keyedWrappers){
            dao.save(kw.toTransientObject());
        }
    }

    public static class KeyedWrapper extends Wrapper implements Keyable{

        private KeyedWrapper(TransientObject transientObject){
            super(transientObject);
        }

        @Override
        public String getKey() {
            return this.Key();
        }

        public TransientObject toTransientObject(){
            return this.toObject(TransientObject.class);
        }
    }
}