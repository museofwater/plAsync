package com.plasync.server.models;

import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

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
@Entity
public class User extends Model {

    public static Finder<String,User> find = new Finder<String,User>(
            String.class, User.class
    );

    public static List<User> findAllUsers() {
        return find.all();
    }

    public static User findById(String id)
    {
        return find.byId(id);
    }

    public static boolean exists(String username) {
        return find
                .where()
                .eq("username", username)
                .findRowCount() > 0;
    }

    @Id
    public String id;

    /**
     * A human readable/recognizable identifier to use when searching for and inviting friends.
     */
    @Column(unique=true)
    public String username;

    public User(String id)
    {
        this.id = id;
    }





}
