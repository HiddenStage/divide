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
