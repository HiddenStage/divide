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

package io.divide.client.android.mock;

import android.content.Context;
import android.content.SharedPreferences;
import com.jug6ernaut.debugdrawer.preference.StringPreference;
import io.divide.client.android.AndroidConfig;
import io.divide.client.android.AndroidModule;

public class AndroidDebugConfig extends AndroidConfig {

    private static String ENDPOINT_TAG = "endpointMode";
    private static String PREF_NAME = "endpoint_pref";
    private static String defaultModule = ModuleType.Prod.name();
    private StringPreference endpointMode;
    private String prodUrl;
    private String devUrl;

    public static enum ModuleType {
        Prod,
        Dev,
        Mock;

        public static <AModule extends AndroidModule> Class<AModule> getModule(ModuleType type){
            switch (type){
                case Prod: return (Class<AModule>) AndroidModule.class;
                case Dev: return (Class<AModule>) AndroidModule.class;
                case Mock: return (Class<AModule>) MockAndroidModule.class;
            }
            return null;
        }
    }

    public AndroidDebugConfig(android.app.Application application, String prodUrl, String devURL) {
        super(application, getCurrentUrl(application,prodUrl,devURL), getCurrentModule(application));

        this.prodUrl = prodUrl;
        this.devUrl = devURL;

        endpointMode = new StringPreference(getMockPreferences(application),ENDPOINT_TAG,defaultModule);
    }

    public void setModuleType(ModuleType type){
        endpointMode.set(type.name());
        this.setModule(ModuleType.getModule(type));
        serverUrl = getCurrentUrl(endpointMode,prodUrl,devUrl);
    }

    public String getCurrentModuleType(){
        return endpointMode.get();
    }

    public String getProdUrl() {
        return prodUrl;
    }

    public String getDevUrl() {
        return devUrl;
    }

    private static String getCurrentUrl(Context context, String prodUrl, String devUrl){
        StringPreference endpointMode = new StringPreference(getMockPreferences(context),ENDPOINT_TAG,defaultModule);
        return getCurrentUrl(endpointMode,prodUrl,devUrl);
    }

    private static String getCurrentUrl(StringPreference endpointMode, String prodUrl, String devUrl){
        ModuleType endpoint = ModuleType.valueOf(endpointMode.get());
        switch (endpoint){
            case Prod: return prodUrl;
            case Dev:  return devUrl;
            case Mock: return "Mock";
            default: return "default";
        }
    }

    private static Class<AndroidModule> getCurrentModule(Context context){
        StringPreference mockMode = new StringPreference(getMockPreferences(context),ENDPOINT_TAG, defaultModule);
        return getCurrentModule(mockMode);
    }

    private static Class<AndroidModule> getCurrentModule(StringPreference modulePref){
        return ModuleType.getModule(ModuleType.valueOf(modulePref.get()));
    }

    private static SharedPreferences getMockPreferences(Context context){
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }
}
