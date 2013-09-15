package org.plasync.server.models;

import play.db.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 * Created with IntelliJ IDEA.
 * User: ericwood
 * Date: 9/4/13
 * Time: 7:13 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * This class stores the GCM notification keys and associates them with users.
 *
 * Notification keys are a new feature in GCM that allow for multiple notifications to be sent to all of the users
 * device with a single request.  The GCM servers will also stop delivery of a notification once the user has dismissed
 * that notification on a device.
 */
@Entity
public class NotificationKey extends Model {

    @Id
    private long id;

    @OneToOne
    private User user;

    private String notificationKey;

    public long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getNotificationKey() {
        return notificationKey;
    }

    public void setNotificationKey(String notificationKey) {
        this.notificationKey = notificationKey;
    }
}
