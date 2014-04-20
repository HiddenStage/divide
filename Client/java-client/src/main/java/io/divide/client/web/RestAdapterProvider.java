//package io.divide.client.web;
//
//import com.google.gson.Gson;
//import com.google.inject.Inject;
//import com.google.inject.Provider;
//import com.jug6ernaut.android.logging.Logger;
//import io.divide.client.Backend;
//import io.divide.client.BackendConfig;
//import retrofit.Profiler;
//import retrofit.RequestInterceptor;
//import retrofit.RestAdapter;
//import retrofit.client.OkClient;
//import retrofit.converter.GsonConverter;
//
//import javax.inject.Singleton;
//
///**
// * Created by williamwebb on 4/9/14.
// */
//@Singleton
//public class RestAdapterProvider implements Provider<RestAdapter> {
//    private static Logger retrologger = Logger.getLogger("Retrofit");
//
//    @Inject BackendConfig config;
//    @Inject Gson gson;
//
//    @Override
//    public RestAdapter get() {
//        RestAdapter.Builder builder = new RestAdapter.Builder();
//        builder.setClient( new OkClient( config.client ) )
//                .setEndpoint(config.serverUrl)
//                .setLogLevel(RestAdapter.LogLevel.FULL)
//                .setLog(new RestAdapter.Log() {
//                    @Override
//                    public void log(String s) {
//                        retrologger.debug(s);
//                    }
//                })
//                .setConverter(new GsonConverter(gson))
//                .setRequestInterceptor(new RequestInterceptor() {
//                    @Override
//                    public void intercept(RequestFacade requestFacade) {
//                        onRequest(requestFacade);
//                    }
//                })
//                .setProfiler(new Profiler() {
//                    @Override
//                    public Object beforeCall() {
//                        return null;
//                    }
//
//                    @Override
//                    public void afterCall(RequestInformation requestInformation, long l, int i, Object o) {
//                        retrologger.error("afterCall(" + requestInformation.getRelativePath() + ":" + requestInformation.getMethod() + ": " + i + " : " + o);
//                        requestEventPublisher.onNext(new RequestObject(requestInformation,l,i,o));
//                    }
//                });
//
//        return builder.build();
//    }
//
//}
