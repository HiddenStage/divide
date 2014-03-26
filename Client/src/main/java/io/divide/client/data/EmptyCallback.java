package io.divide.client.data;

import com.jug6ernaut.android.logging.Logger;
import retrofit.client.Response;

/**
 * Created by williamwebb on 11/25/13.
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