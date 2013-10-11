package org.plasync.client.android.gcm;

import android.app.IntentService;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.plasync.client.android.dal.GcmSettingsDao;
import org.plasync.client.android.model.GcmSettings;

/**
 * Created by ericwood on 9/21/13.
 */
public class GcmReceiveIntentLauncher extends IntentService {
    private static final String TAG = GcmReceiveIntentLauncher.class.getName();

    private GcmSettingsDao gcmSettingsDao;


    public GcmReceiveIntentLauncher() {
        super(GcmReceiveIntentLauncher.class.getName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        gcmSettingsDao = new GcmSettingsDao(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM will be
             * extended in the future with new message types, just ignore any message types you're
             * not interested in, or that you don't recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                Log.i(TAG, "GCM send error: " + extras.toString());
            }
            else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                Log.i(TAG, "GCM messages deleted on server: " + extras.toString());
            }
            // If it's a regular GCM message, get the registered receive intent for the app
            else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                Log.i(TAG, "GCM message received: " + extras.toString());
                ComponentName receiveIntentComponentName = getReceiveIntentComponentName();
                Intent receiveIntent = new Intent();
                receiveIntent.putExtras(extras);
                receiveIntent.setComponent(receiveIntentComponentName);
                startService(receiveIntent);
            }
        }
    }

    private ComponentName getReceiveIntentComponentName() {
        // Get the Gcm Settings for the app
        GcmSettings gcmSettings = gcmSettingsDao.getGcmSettingsForApp(getPackageName());
        return gcmSettings != null ?
                new ComponentName(getPackageName(),gcmSettings.getReceiveIntentName()) : null;
    }
}
