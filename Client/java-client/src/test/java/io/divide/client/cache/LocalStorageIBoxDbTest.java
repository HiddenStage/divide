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