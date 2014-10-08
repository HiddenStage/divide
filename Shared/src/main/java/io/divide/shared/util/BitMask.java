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

package io.divide.shared.util;

import java.io.Serializable;

public class BitMask implements Serializable {

    private int flags = 0;

//  private final void setclear(int mask, boolean set) { if (set) set(mask); else clear(mask); }

    public BitMask(Integer... set){
        for(Integer i : set){
            set(i);
        }
    }

    public void set(int mask, boolean set){
        if(set)set(mask);
        else clear(mask);
    }

    private final void set(int mask) { flags |= 1 << mask; }
    public final void clear(int mask) { flags &= ~(1 << mask); }
    public final boolean get(int mask) { return (flags & 1 << mask) != 0; }

    public int getFlags(){
        return flags;
    }

    /*

    Set union
        A | B
    Set intersection
        A & B
    Set subtraction
        A & ~B
    Set negation
        ALL_BITS ^ A
    Set bit
        A |= 1 << bit
    Clear bit
        A &= ~(1 << bit)
    Test bit
        (A & 1 << bit) != 0

     */
}




