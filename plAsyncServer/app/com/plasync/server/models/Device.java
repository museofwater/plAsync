package com.plasync.server.models;

/**
 * Created with IntelliJ IDEA.
 * User: ericwood
 * Date: 9/4/13
 * Time: 8:02 PM
 * To change this template use File | Settings | File Templates.
 */

import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import java.util.List;

/**
 * Represents a device (Android, iOS, or Facebook)
 */
@Entity
public class Device extends Model {

    public static Finder<Long,Device> find = new Finder<Long,Device>(
            Long.class, Device.class
    );

    @Id
    private long id;

    @OneToOne
    private User user;

    private DeviceType type;

    private String gcmId;

    public static List<Device> findByUser(String userId) {
        return find
                .where()
                .eq("user.id",userId)
                .findList();
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

    public DeviceType getType() {
        return type;
    }

    public void setType(DeviceType type) {
        this.type = type;
    }

    public String getGcmId() {
        return gcmId;
    }

    public void setGcmId(String gcmId) {
        this.gcmId = gcmId;
    }
}
