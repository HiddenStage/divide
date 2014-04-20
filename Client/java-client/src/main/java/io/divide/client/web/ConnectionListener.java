package io.divide.client.web;

import rx.Observer;

/**
 * Created by williamwebb on 4/9/14.
 */
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
