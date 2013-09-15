package org.plasync.server.service;

/**
 * Created with IntelliJ IDEA.
 * User: ericwood
 * Date: 9/5/13
 * Time: 6:28 AM
 * To change this template use File | Settings | File Templates.
 */
public class InvalidAppSpecificationException extends Exception {
    public InvalidAppSpecificationException(String message) {
        super(message);
    }
}
