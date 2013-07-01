package com.plasync.server.model;

import play.Logger;
import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created with IntelliJ IDEA.
 * User: ericwood
 * Date: 6/29/13
 * Time: 1:06 PM
 * To change this template use File | Settings | File Templates.
 */

@Entity
public class User extends Model {

//    public static User findByOpenId(String openId)
//    {
//        Logger.debug("finding by " + openId);
//        User user = User.find("openId", openId).first();
//        if (user == null)
//        {
//            Logger.debug("creating new User");
//            user = new User(openId);
//            user.save();
//        }
//        return user;
//    }

    @Id
    public String openId;

    public User(String openId)
    {
        this.openId = openId;
    }



}
