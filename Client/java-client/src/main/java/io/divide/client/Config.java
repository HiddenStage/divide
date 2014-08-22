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

import com.squareup.okhttp.OkHttpClient;
import rx.Scheduler;
import rx.schedulers.Schedulers;

import java.lang.reflect.InvocationTargetException;

public abstract class Config<BackendType extends Backend> {

    public String fileSavePath;
    public String serverUrl;
    public final long id = System.currentTimeMillis();
    public final OkHttpClient client = new OkHttpClient();
    private Scheduler subscribeOn = Schedulers.io();
    private Scheduler observeOn = Schedulers.io();
    private BackendModule backendModule;

    /**
     * Returns a concrete class type used to initialize the specific Backend class.
     * @return
     */
    public abstract Class<BackendType> getModuleType();

    /**
     * @param fileSavePath base file storage location to be used by Divide.
     * @param url url used to connect to remote Divide server.
     */
    public Config(String fileSavePath, String url){
        this(fileSavePath, url, BackendModule.class);
    }

    /**
     *
     * @param fileSavePath base file storage location to be used by Divide.
     * @param url url used to connect to remote Divide server.
     * @param moduleClass module class type to be used by Divide.
     */
    protected <ModuleType extends BackendModule> Config(String fileSavePath, String url, Class<ModuleType> moduleClass){
        this.fileSavePath = fileSavePath;
        this.serverUrl = url;

        setModule(moduleClass);
    }

    /**
     * Sets @see scheduler Divide callbacks will run on
     * @param observeOn scheduler to which Divide callbacks will run on
     */
    public Config observeOn(Scheduler observeOn){
        this.observeOn = observeOn;
        return this;
    }

    /**
     * Sets @see schedule Divide operations will run on
     * @param subscribeOn scheduler to which Divide operations will run on
     * @return
     */
    public Config subscribeOn(Scheduler subscribeOn){
        this.subscribeOn = subscribeOn;
        return this;
    }

    public Scheduler observeOn() {
        return observeOn;
    }

    public Scheduler subscribeOn() {
        return subscribeOn;
    }

    public final BackendModule getModule(){
        return backendModule;
    }

    public final <ModuleType extends BackendModule> void setModule(Class<ModuleType> moduleClass){
        try {
            this.backendModule = createInstance(moduleClass,this);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException(e);
        }
    }

    private static <B extends BackendModule, C extends Backend> B createInstance(Class<B> type, Config<C> config) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        B b = type.newInstance();
        b.init(config);
        return b;
    }

    @Override
    public String toString() {
        return "Config{" +
                "fileSavePath='" + fileSavePath + '\'' +
                ", serverUrl='" + serverUrl + '\'' +
                ", id=" + id +
                ", client=" + client +
                ", subscribeOn=" + subscribeOn +
                ", observeOn=" + observeOn +
                ", backendModule=" + backendModule +
                '}';
    }
}
