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

package io.divide.client;

import com.google.inject.Inject;
import io.divide.client.cache.LocalStorageIBoxDb;
import io.divide.shared.transitory.TransientObject;

import java.io.File;

public class MockLocalStorage <T1 extends TransientObject,T2 extends TransientObject> extends LocalStorageIBoxDb<T1,T2> {

    @Inject
    public MockLocalStorage(Config config){
        super(getPath(config.fileSavePath));
    }

    private static String getPath(String path){
        File f = new File(path + (path.length()>0?"/":"") + getCount());
        f.mkdirs();
        return f.getPath();
    }

    private static int count=0;
    private static synchronized int getCount(){
        return count++;
    }
}
