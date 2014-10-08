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

package io.divide.shared.transitory;

import com.google.gson.Gson;
import io.divide.shared.util.BitMask;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class FilePermissions {

    public static enum Level {
        OWNER(4),
        GROUP(2),
        WORLD(0);

        private int offset;
        private Level(int offset){
            this.offset = offset;
        }
    }

    //                                         Owner
    //                                         | Group
    //                                         | | World
    //                                         | | |
    //                                         RWRWRW
    //                                         543210
    private BitMask permissions = new BitMask(5,4);
    private Set<String> groups = new HashSet<String>();

    public boolean isReadable(Level level){
        return permissions.get(level.offset);
    }

    public boolean isWritable(Level level){
        return permissions.get(level.offset+1);
    }

    public void setReadable(boolean readable, Level... levels){
        for(Level l : levels){
            if (l.equals(Level.OWNER)) continue;
            permissions.set(l.offset,readable);
        }
    }

    public void setWritable(boolean writable, Level... levels){
        for(Level l : levels){
            if (l.equals(Level.OWNER)) continue;
            permissions.set(l.offset+1,writable);
        }
    }

    private void setGroups(String... groups){
        this.groups.clear();
        this.groups.addAll(Arrays.asList(groups));
    }

    public Set<String> getGroups(){
        return groups;
    }

//    private static BitSet fromString(final String s) {
//        return BitSet.valueOf(new long[] { Long.parseLong(s, 2) });
//    }
//
//    private static String toString(BitSet bs) {
//        return Long.toString(bs.toLongArray()[0], 2);
//    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}
