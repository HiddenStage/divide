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