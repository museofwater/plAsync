package com.plasync.server.service;

/**
 * Created with IntelliJ IDEA.
 * User: ericwood
 * Date: 9/2/13
 * Time: 3:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class NotFoundException extends Exception {
    public NotFoundException(String message) {
        super(message);
    }
}
