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

package io.divide.client.android;

import android.app.Application;
import io.divide.client.Config;
import io.divide.client.android.security.PRNGFixes;
import rx.android.schedulers.AndroidSchedulers;

import java.io.File;

public class AndroidConfig extends Config<AndroidBackend>{

    static { PRNGFixes.apply(); }

    public Application app;

    public AndroidConfig(Application application, String url) {
        this(application, url, AndroidModule.class);
    }

    protected AndroidConfig(Application application, String url, Class<AndroidModule> type){
        super(application.getFilesDir().getPath() + File.separator, url, type);
        this.app = application;
        this.observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Class<AndroidBackend> getModuleType() {
        return AndroidBackend.class;
    }
}
