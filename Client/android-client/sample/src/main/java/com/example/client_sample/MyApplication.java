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

package com.example.client_sample;

import android.app.Application;
import android.os.Handler;
import android.widget.Toast;
import com.jug6ernaut.android.logging.ALogger;
import com.jug6ernaut.android.logging.Logger;
import io.divide.client.Backend;
import io.divide.client.android.AndroidBackend;
import io.divide.client.android.mock.AndroidDebugConfig;
import io.divide.client.android.push.PushEvent;
import io.divide.client.android.push.PushListener;

public class MyApplication extends Application {

    private static Logger logger;
    private Handler handler = new Handler();

    @Override
    public void onCreate(){
        ALogger.init(this, "Backend", true);
        logger = Logger.getLogger(MyApplication.class);

        AndroidBackend b = Backend.init(new AndroidDebugConfig(this,getProdUrl(),getDevUrl()));
//        BackendServices.addPushListener(listener);
    }

    @Override
    public void onTerminate() {

    }

    private String getProdUrl(){
        return getString(R.string.prodUrl);
    }

    private String getDevUrl(){
        return getString(R.string.devUrl);
    }

    private PushListener listener = new PushListener() {

        @Override
        public void onEvent(final PushEvent event) {
            logger.debug("Push Message: " + event);
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(MyApplication.this, "Push Message: " + event, Toast.LENGTH_LONG).show();
                }
            });
        }
    };
}
