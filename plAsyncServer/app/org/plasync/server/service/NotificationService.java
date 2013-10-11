package org.plasync.server.service;

import org.plasync.server.AppNotFoundException;
import org.plasync.server.gcm.GcmMessage;
import org.plasync.server.gcm.GcmSender;
import org.plasync.server.models.App;
import org.plasync.server.models.Credentials;
import org.plasync.server.models.FriendAssociation;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ericwood
 * Date: 8/31/13
 * Time: 8:10 PM
 * To change this template use File | Settings | File Templates.
 */

/**
 * Service that handles notifying users of events
 *
 * This service delegates actual notification to device specific services
 */
public class NotificationService {

    public static void sendFriendRequest(FriendAssociation newFriendRequest) throws NotificationException {
        List<App> apps = getApps(newFriendRequest);

        AndroidNotificationService.sendFriendRequest(apps, newFriendRequest);
        IosNotificationService.sendFriendRequest(apps);
        FacebookNotificationService.sendFriendRequest(apps);

    }

    public static void sendFriendRequestAccepted(FriendAssociation friendRequest) throws NotificationException {

        // Get the app id
        List<App> apps = getApps(friendRequest);

        AndroidNotificationService.sendFriendRequestAccepted(apps);
        IosNotificationService.sendFriendRequestAccepted(apps);
        FacebookNotificationService.sendFriendRequestAccepted(apps);
    }

    private static List<App> getApps(FriendAssociation friendRequest) {
        // Get the app id
        String appId = friendRequest.getAppId();

        // Find all apps with that id and requested user
        return App.findByUserAndApp(friendRequest.getFriend().getId(), appId);
    }
}
