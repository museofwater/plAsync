package org.plasync.server.service;

import org.plasync.server.gcm.GcmConstants;
import org.plasync.server.gcm.GcmMessage;
import org.plasync.server.gcm.GcmMessageConstants;
import org.plasync.server.gcm.GcmSender;
import org.plasync.server.models.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ericwood
 * Date: 9/24/13
 * Time: 6:29 AM
 * To change this template use File | Settings | File Templates.
 */
public class AndroidNotificationService {
    private static final GcmSender gcmSender = new GcmSender(Credentials.getGcmApiKey());

    public static void sendFriendRequest(List<App> apps, FriendAssociation request) throws NotificationException {
        List<String> regIds = getRegistrationIds(apps);
        if (!regIds.isEmpty()) {
            // Build a message
            GcmMessage message = new GcmMessage.Builder()
                    .collapseKey(GcmMessageConstants.FRIEND_REQUEST_COLLAPSE_KEY)
                    .addData(GcmMessageConstants.REQUESTOR_NAME, request.getUser().getUsername()).build();
            try {
                gcmSender.send(message,regIds, GcmConstants.NUM_RETRIES);
            }
            catch (IOException e) {
                throw new NotificationException("Error sending GCM notification",e);
            }
        }
    }

    public static void sendFriendRequestAccepted(List<App> apps) throws NotificationException {
        List<String> regIds = getRegistrationIds(apps);
        if (!regIds.isEmpty()) {
            // Build a message
            GcmMessage message = new GcmMessage.Builder()
                    .collapseKey(GcmMessageConstants.FRIEND_REQUEST_ACCEPTED_COLLAPSE_KEY)
                    .addData(GcmMessageConstants.FRIEND_NAME, apps.get(0).getUser().getUsername()).build();
            try {
                gcmSender.send(message,regIds, GcmConstants.NUM_RETRIES);
            }
            catch (IOException e) {
                throw new NotificationException("Error sending GCM notification",e);
            }
        }
    }

    private static List<String> getRegistrationIds(List<App> apps) {
        List<String> regIds = new ArrayList<String>();
        for (App app : apps) {
            regIds.add(app.getGcmId());
        }
        return regIds;
    }
}
