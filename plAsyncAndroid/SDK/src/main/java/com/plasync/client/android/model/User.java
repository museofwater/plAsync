package com.plasync.client.android.model;

import com.google.gson.annotations.Expose;

/**
 * Created with IntelliJ IDEA.
 * User: ericwood
 * Date: 6/29/13
 * Time: 1:06 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * A plAsync user.  Users are identified by an open id so that the server can recognize the user regardless of what
 * device/app they are coming from.
 */
public class User {

    @Expose
    private String id;

    /**
     * A human readable/recognizable identifier to use when searching for and inviting friends.
     */
    @Expose
    private String username;

    public User(String id, String username)
    {
        this.id = id;
        this.username = username;
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        User user = (User) o;

        if (!id.equals(user.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + id.hashCode();
        return result;
    }
}
