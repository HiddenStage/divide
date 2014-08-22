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

public final class BackendConfig extends Config<Backend>{

    /**
     * Default @see Config implementation used by Divide. Used for the default implementation returning a @see Backend object.
     * @return
     */

    @Override
    public Class<Backend> getModuleType() {
        return Backend.class;
    }

    public BackendConfig(String fileSavePath, String url){
        this(fileSavePath, url, BackendModule.class);
    }

    public <ModuleType extends BackendModule> BackendConfig(String fileSavePath, String url, Class<ModuleType> moduleClass){
        super(fileSavePath,url,moduleClass);
    }
}
