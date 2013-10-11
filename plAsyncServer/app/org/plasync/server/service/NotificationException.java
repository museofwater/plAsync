package org.plasync.server.service;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: ericwood
 * Date: 10/1/13
 * Time: 6:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class NotificationException extends Exception {
    public NotificationException(String s, Exception e) {
        super(s,e);
    }
}
