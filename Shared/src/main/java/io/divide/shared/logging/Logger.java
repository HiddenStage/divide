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
