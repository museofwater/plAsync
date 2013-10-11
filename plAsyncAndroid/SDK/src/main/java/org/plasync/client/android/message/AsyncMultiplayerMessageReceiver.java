package org.plasync.client.android.message;

import android.app.IntentService;
import android.content.Intent;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.plasync.client.android.gcm.GcmBroadcastReceiver;
import org.plasync.client.android.gcm.GcmMessageConstants;
import org.plasync.client.android.model.FriendRequest;

/**
 * Created by ericwood on 10/2/13.
 */
public abstract class AsyncMultiplayerMessageReceiver extends IntentService {

    public AsyncMultiplayerMessageReceiver(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Get message
        String key = intent.getStringExtra(GcmMessageConstants.COLLAPSE_KEY);
        if (key.equalsIgnoreCase(GcmMessageConstants.FRIEND_REQUEST_COLLAPSE_KEY)) {
            FriendRequestMessage msg = new FriendRequestMessage(intent.getStringExtra(GcmMessageConstants.REQUESTOR_NAME));
            onFriendRequestReceived(msg);
        }
        // Release the wake lock
        notifyCompleted(intent);
    }

    protected abstract void onFriendRequestReceived(FriendRequestMessage msg);
    protected abstract void onFriedRequestAccepted(FriendRequestAcceptedMessage msg);

    private void notifyCompleted(Intent intent) {
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);

    }
}
