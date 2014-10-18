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

package io.divide.server.dao;

import io.divide.shared.transitory.Credentials;
import io.divide.shared.transitory.TransientObject;

import java.util.Map;

public class ServerObject extends TransientObject {

    private static Credentials user = new SystemUser();

    @Override
    protected final Credentials getLoggedInUser(){
        return user;
    }

    public void setMaps(Map<String,Object> userData, Map<String,String> metaData){
        this.user_data = userData;
        this.meta_data = metaData;
    }

    private static class SystemUser extends Credentials {

        @Override
        protected boolean isSystemUser(){
            return true;
        }

    }
}
