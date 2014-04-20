package io.divide.shared.logging;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by williamwebb on 4/19/14.
 */

/*
 Stubbed out for now
 */
public class Logger {
    public static boolean FORCE_LOGGING = true;

    private Logger(){};

    public static Logger getLogger(String name) {
        return new DLogger();
    }

    public static Logger getLogger(Class clazz) { return new DLogger(); }

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
        public void trace(Object message) { System.out.println(message); }
        public void trace(Object message, Throwable t) {
            System.err.println(message);
            System.err.println(getStackTrace(t));
        }
        public void debug(Object message) { System.out.println(message); }
        public void debug(Object message, Throwable t) {
            System.err.println(message);
            System.err.println(getStackTrace(t));
        }
        public void info(Object message) { System.out.println(message); }
        public void info(Object message, Throwable t) {
            System.err.println(message);
            System.err.println(getStackTrace(t));
        }
        public void warn(Object message) { System.err.println(message); }
        public void warn(Object message, Throwable t) {
            System.err.println(message);
            System.err.println(getStackTrace(t));
        }
        public void error(Object message) { System.err.println(message); }
        public void error(Object message, Throwable t) {
            System.err.println(message);
            System.err.println(getStackTrace(t));
        }
        public void fatal(Object message) { System.err.println(message); }
        public void fatal(Object message, Throwable t) {
            System.err.println(message);
            System.err.println(getStackTrace(t));
        }
    }

    private static String getStackTrace(Throwable throwable) {
        StringWriter sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sw));
        return sw.toString();
    }

}
