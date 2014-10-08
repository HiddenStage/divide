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

import io.divide.dao.TestObject1;
import org.junit.Test;

import java.util.HashMap;

public class BackendToOfyTest {
    @Test
    public void testGetOfy() throws Exception {
        TestObject1 object = new TestObject1();
        BackendToOfy.getOfy(object);
    }

    @Test
    public void testGetBack() throws Exception {
        OfyObject object = new OfyObject("somekey",new HashMap<String, Object>(),new HashMap<String, String>());
        BackendToOfy.getBack(object);
    }
}
