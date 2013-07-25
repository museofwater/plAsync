package com.plasync.client.android;

/**
 * Created with IntelliJ IDEA.
 * User: ericwood
 * Date: 7/20/13
 * Time: 3:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class AsyncMultiplayerUser {
    private String id;
    private String username;

    public AsyncMultiplayerUser(String id, String username) {
        this.id = id;
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
