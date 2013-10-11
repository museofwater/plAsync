package org.plasync.client.android.gcm;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

/**
 * Created by ericwood on 9/8/13.
 */
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Explicitly specify that GcmIntentService will handle the intent.
        ComponentName receiveIntentLauncherComponent =
                new ComponentName(context.getPackageName(),
                                  GcmReceiveIntentLauncher.class.getName());
        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, (intent.setComponent(receiveIntentLauncherComponent)));
        setResultCode(Activity.RESULT_OK);
    }
}
