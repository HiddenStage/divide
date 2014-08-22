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

//package io.divide.client.debug.push;
//
//import com.google.inject.Inject;
//import io.divide.client.BackendConfig;
//import io.divide.client.push.PushManager;
//import io.divide.client.push.PushWebService;
//
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
