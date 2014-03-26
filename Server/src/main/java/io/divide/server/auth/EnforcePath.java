package io.divide.server.auth;

import java.lang.annotation.*;

/**
 * Created with IntelliJ IDEA.
 * User: williamwebb
 * Date: 8/16/13
 * Time: 10:20 AM
 */
@Inherited
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface EnforcePath {
}
