package com.plasync.client.android.model;

/**
 * Created with IntelliJ IDEA.
 * User: ericwood
 * Date: 7/20/13
 * Time: 3:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class AsyncMultiplayerUser {
    private String serverUrl;
    private long id;
    private String userId;

    private String username;

    /**
     * User when creating a non-persistent instance
     */
    public AsyncMultiplayerUser() {
    }

    /**
     * User when creating a user retrieved from the database
     * @param id
     */
    public AsyncMultiplayerUser(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
