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




