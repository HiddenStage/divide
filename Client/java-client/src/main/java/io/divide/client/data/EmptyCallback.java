package io.divide.client.data;

import io.divide.shared.logging.Logger;
import retrofit.client.Response;

/**
 * Created by williamwebb on 11/25/13.
 *
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