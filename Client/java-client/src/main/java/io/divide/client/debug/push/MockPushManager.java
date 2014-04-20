//package io.divide.client.debug.push;
//
//import com.google.inject.Inject;
//import io.divide.client.BackendConfig;
//import io.divide.client.push.PushManager;
//import io.divide.client.push.PushWebService;
//
///**
// * Created by williamwebb on 4/6/14.
// */
//public class MockPushManager extends PushManager {
//
//    @Inject
//    MockPushWebService mockPushWebService;
//
//    @Inject
//    public MockPushManager(BackendConfig config) {
//        super(config);
//    }
//
//    @Override
//    public PushWebService getWebService(){
//        return mockPushWebService;
//    }
//
//    @Override
//    public void initAdapter(BackendConfig config){};
//}
