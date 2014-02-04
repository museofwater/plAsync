package org.plasync.server.service;

import org.plasync.server.AppNotFoundException;
import org.plasync.server.gcm.GcmMessage;
import org.plasync.server.gcm.GcmSender;
import org.plasync.server.models.App;
import org.plasync.server.models.Credentials;
import org.plasync.server.models.FriendAssociation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
 * This service delegates actual notification to device specific services.
 *
 * Notification errors returned by these services are collected and logged by this service as well as being passed on
 * to the caller.  The assumption is that notification errors should be recorded here in case the caller chooses not to
 * propagate the error.
 */
public class NotificationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationService.class);

    private static final String NOTIFY_LOG_TAG = "NOTIFY:";
    private static final String NEW_FRIEND_REQUEST_TAG = "NewFriendRequest";
    private static final String FRIEND_REQUEST_ACCEPTED_TAG = "FriendRequestAccepted";

    public static void sendFriendRequest(FriendAssociation newFriendRequest) throws NotificationException {
        List<App> apps = getApps(newFriendRequest);

        try {
            AndroidNotificationService.sendFriendRequest(apps, newFriendRequest);
            IosNotificationService.sendFriendRequest(apps);
            FacebookNotificationService.sendFriendRequest(apps);
        }
        catch (NotificationException e) {
            logNotificationError(e);
            throw e;
        }

    }

    public static void sendFriendRequestAccepted(FriendAssociation friendRequest) throws NotificationException {

        // Get the app id
        List<App> apps = getApps(friendRequest);

        try {
            AndroidNotificationService.sendFriendRequestAccepted(apps);
            IosNotificationService.sendFriendRequestAccepted(apps);
            FacebookNotificationService.sendFriendRequestAccepted(apps);
        }
        catch (NotificationException e) {
            logNotificationError(e);
            throw e;
        }
    }

    private static List<App> getApps(FriendAssociation friendRequest) {
        // Get the app id
        String appId = friendRequest.getAppId();

        // Find all apps with that id and requested user
        return App.findByUserAndApp(friendRequest.getFriend().getId(), appId);
    }

    private static void logNotificationError(NotificationException e) {
        LOGGER.error(NOTIFY_LOG_TAG + NEW_FRIEND_REQUEST_TAG,e);
    }
}
