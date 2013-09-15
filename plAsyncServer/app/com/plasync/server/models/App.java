package com.plasync.server.models;

/**
 * Created with IntelliJ IDEA.
 * User: ericwood
 * Date: 9/15/13
 * Time: 4:19 PM
 * To change this template use File | Settings | File Templates.
 */

import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.util.List;

/**
 * Represents the association between a user and an app
 */
@Entity
public class App extends Model {

    public static Finder<Long,App> find = new Finder<Long,App>(
            Long.class, App.class
    );

    public static List<App> findByUserAndApp(String userId, String appId) {
        return find
                .where()
                .eq("user.id", userId)
                .eq("appId", appId)
                .findList();
    }

    @Id
    private long id;

    @OneToOne
    private User user;

    private String appId;

    private DeviceType deviceType;

    private String gcmId;

    public long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }

    public String getGcmId() {
        return gcmId;
    }

    public void setGcmId(String gcmId) {
        this.gcmId = gcmId;
    }

}
