package org.plasync.client.android.model;

/**
 * Created with IntelliJ IDEA.
 * User: ericwood
 * Date: 9/4/13
 * Time: 8:02 PM
 * To change this template use File | Settings | File Templates.
 */

import com.google.gson.annotations.Expose;

/**
 * Represents a device (Android, iOS, or Facebook)
 */
public class Device {

    @Expose
    private User user;

    @Expose
    private DeviceType type;

    @Expose
    private String gcmId;

    public Device(User user, String gcmId) {
        // Since we are on android force device type to android
        this.type = DeviceType.ANDROID;
        this.user = user;
        this.gcmId = gcmId;
    }
}
