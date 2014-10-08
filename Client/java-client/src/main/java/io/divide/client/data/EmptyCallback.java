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

package io.divide.client.data;

import io.divide.shared.logging.Logger;
import retrofit.client.Response;

/*
 * Callback to be provided when no actual callback is needed.
 */
public class EmptyCallback<B> implements retrofit.Callback<B> {
    private static Logger logger = Logger.getLogger(EmptyCallback.class);
    @Override
    public void success(B bs, Response response) {

    }

    @Override
    public void failure(retrofit.RetrofitError retrofitError) {
        logger.fatal("",retrofitError);
    }
};