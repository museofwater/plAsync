package org.plasync.client.android.model;

/**
 * Created with IntelliJ IDEA.
 * User: ericwood
 * Date: 9/15/13
 * Time: 4:19 PM
 * To change this template use File | Settings | File Templates.
 */

import com.google.gson.annotations.Expose;

/**
 * Represents the association between a user and an app
 */
public class App {


    @Expose
    private long id;

    @Expose
    private User user;

    @Expose
    private String appId;

    @Expose
    private DeviceType deviceType;

    @Expose
    private String gcmId;

    public App(User user, String appId, DeviceType deviceType, String gcmId) {
        this.user = user;
        this.appId = appId;
        this.deviceType = deviceType;
        this.gcmId = gcmId;
    }

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
