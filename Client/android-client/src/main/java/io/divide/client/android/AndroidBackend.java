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

import com.google.inject.Inject;
import io.divide.client.Backend;
import io.divide.client.android.push.PushManager;

public class AndroidBackend extends Backend {

    @Inject PushManager pushManager;
    @Inject AndroidConfig config;

    @Inject
    private AndroidBackend(){
        super();
    }

}
