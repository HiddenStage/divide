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