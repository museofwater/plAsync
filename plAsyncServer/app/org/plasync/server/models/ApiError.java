package org.plasync.server.models;

/**
 * Created with IntelliJ IDEA.
 * User: ericwood
 * Date: 9/16/13
 * Time: 3:35 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * An error that can be returned to a client in case of a client usage error
 */
public class ApiError {

    private String type;
    private String message;

    public ApiError(String type, String message) {
        this.type = type;
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
