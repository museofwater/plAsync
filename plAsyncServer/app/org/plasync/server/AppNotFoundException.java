package org.plasync.server;

/**
 * Created with IntelliJ IDEA.
 * User: ericwood
 * Date: 9/16/13
 * Time: 3:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class AppNotFoundException extends Throwable {
    public AppNotFoundException(String message) {
        super(message);
    }
}
