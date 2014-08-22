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

package io.divide.shared.transitory.query;

import io.divide.shared.transitory.TransientObject;

public enum SelectOperation {
    COUNT(Count.class);

    private transient Class<?> type;

    private <T extends TransientObject> SelectOperation(Class<T> type){
        this.type = type;
    }

    public Class<?> getType(){
        return type;
    }

    public String getErrorMessage(){
        return this.name() + " requires type " + type.getSimpleName();
    }


}
