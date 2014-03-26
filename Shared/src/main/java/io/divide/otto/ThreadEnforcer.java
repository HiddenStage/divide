/*
 * Copyright (C) 2012 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.divide.otto;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Enforces a thread confinement policy for methods on a particular event bus.
 *
 * @author Jake Wharton
 */
public interface ThreadEnforcer {

  /**
   * Enforce a valid thread for the given {@code bus}. Implementations may throw any runtime exception.
   *
   * @param bus Event bus instance on which an action is being performed.
   */
  void enforce(Bus bus);


  /** A {@link io.divide.otto.ThreadEnforcer} that does no verification. */
  ThreadEnforcer ANY = new ThreadEnforcer() {
    @Override public void enforce(Bus bus) {
      // Allow any thread.
    }
  };

  /** A {@link io.divide.otto.ThreadEnforcer} that confines {@link io.divide.otto.Bus} methods to the main thread. */
  ThreadEnforcer ANDROID = new ThreadEnforcer() {
      Object looper;
      Method myLooper;
      Method getMainLooper;
      Class looperClass = null;
      private Boolean onAndroid = null;

    @Override public void enforce(Bus bus) {
        if(onAndroid == null && onAndroid == false){
            try{
                looperClass = Class.forName("android.os.Looper");
                myLooper = looperClass.getMethod("myLooper", null);
                getMainLooper = looperClass.getMethod("getMainLooper",null);
                onAndroid = true;
            } catch (ClassNotFoundException e) {
                onAndroid = false;
            } catch (NoSuchMethodException e) {
                onAndroid = false;
            }
        }

        try {
            if(getLooper(myLooper) != getLooper(getMainLooper)){
                throw new IllegalStateException("Event bus " + bus + " accessed from non-main thread " + getLooper(myLooper));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
      }
    }

    private Object getLooper(Method m) throws InvocationTargetException, IllegalAccessException {
        return m.invoke(null,null);
    }
  };

}
