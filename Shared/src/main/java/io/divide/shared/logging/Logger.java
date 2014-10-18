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

package io.divide.shared.logging;

import java.io.PrintWriter;
import java.io.StringWriter;

/*
 Stubbed out for now
 */
public class Logger {
    public static boolean FORCE_LOGGING = true;

    private Logger(){};

    public static Logger getLogger(String name) {
        return new DLogger(name);
    }

    public static Logger getLogger(Class clazz) { return new DLogger(clazz.getName()); }

    public void trace(Object message) {    }
    public void trace(Object message, Throwable t) {    }
    public void debug(Object message) {    }
    public void debug(Object message, Throwable t) {    }
    public void info(Object message) {    }
    public void info(Object message, Throwable t) {    }
    public void warn(Object message) {    }
    public void warn(Object message, Throwable t) {    }
    public void error(Object message) {    }
    public void error(Object message, Throwable t) {    }
    public void fatal(Object message) {    }
    public void fatal(Object message, Throwable t) {    }

    private static class DLogger extends Logger{
        private String name;
        public DLogger(String name) {
            this.name = name;
        }

        private String format(Object text){ return name + ":" + String.valueOf(text); }

        public void trace(Object message) { System.out.println(format(message)); }
        public void trace(Object message, Throwable t) {
            System.err.println(format(message));
            System.err.println(getStackTrace(t));
        }
        public void debug(Object message) { System.out.println(format(message)); }
        public void debug(Object message, Throwable t) {
            System.err.println(format(message));
            System.err.println(getStackTrace(t));
        }
        public void info(Object message) { System.out.println(format(message)); }
        public void info(Object message, Throwable t) {
            System.err.println(format(message));
            System.err.println(getStackTrace(t));
        }
        public void warn(Object message) { System.err.println(format(message)); }
        public void warn(Object message, Throwable t) {
            System.err.println(format(message));
            System.err.println(getStackTrace(t));
        }
        public void error(Object message) { System.err.println(format(message)); }
        public void error(Object message, Throwable t) {
            System.err.println(format(message));
            System.err.println(getStackTrace(t));
        }
        public void fatal(Object message) { System.err.println(format(message)); }
        public void fatal(Object message, Throwable t) {
            System.err.println(format(message));
            System.err.println(getStackTrace(t));
        }
    }

    private static String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

}
