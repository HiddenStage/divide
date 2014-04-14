package io.divide.client.web;

import static retrofit.RequestInterceptor.RequestFacade;

/**
 * Created by williamwebb on 4/9/14.
 */
public interface OnRequestInterceptor {
    RequestFacade onRequest(RequestFacade requestFacade);
}
