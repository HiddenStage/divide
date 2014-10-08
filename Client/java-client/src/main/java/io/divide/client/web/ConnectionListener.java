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

package io.divide.client.web;

import rx.Observer;

public abstract class ConnectionListener {

    public abstract void onEvent(boolean connected);

    protected Observer<Boolean> getObserver(){ return connectionObserver; }

    private Observer<Boolean> connectionObserver = new Observer<Boolean>() {

        @Override public void onCompleted() { }

        @Override public void onError(Throwable throwable) { }

        @Override
        public void onNext(Boolean aBoolean) {
            onEvent(aBoolean);
        }
    };

}
