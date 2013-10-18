package org.plasync.client.android.testapp;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import org.plasync.client.android.message.AsyncMultiplayerMessageReceiver;
import org.plasync.client.android.message.FriendRequestAcceptedMessage;
import org.plasync.client.android.message.FriendRequestMessage;

/**
 * Created by ericwood on 10/9/13.
 */
public class AsyncMultiplayerTestAppMessageReceiver extends AsyncMultiplayerMessageReceiver {
    private final String TAG = AsyncMultiplayerTestAppMessageReceiver.class.getName();

    public AsyncMultiplayerTestAppMessageReceiver() {
        super(AsyncMultiplayerTestAppMessageReceiver.class.getName());
    }

    @Override
    protected void onFriendRequestReceived(FriendRequestMessage request) {
        createNotification(request);

    }

    @Override
    protected void onFriedRequestAccepted(FriendRequestAcceptedMessage request) {

    }

    private void createNotification(FriendRequestMessage request) {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_notify)
                .setContentTitle("Friend Request")
                .setContentText(request.getRequestorName() + " would like to add you as a friend.");
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(100, mBuilder.build());
    }


}
