package org.plasync.client.android.gcm;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

/**
 * Created by ericwood on 9/5/13.
 */
public class GcmClient {
    private static final String TAG = GcmClient.class.getName();

    private String gcmId = null;
    private final Context context;
    private GoogleCloudMessaging gcm = null;

    public GcmClient(Context context) {
        this.context = context;
    }

    public String getGcmId(String senderId) throws GcmError {
        if (this.gcmId != null) {
            return this.gcmId;
        }
        if (gcm == null) {
            this.gcm = GoogleCloudMessaging.getInstance(context);
        }
        try {
            this.gcmId = gcm.register(senderId);
        }
        catch (IOException e) {
            Log.e(TAG,"Error registering with GCM",e);
            throw new GcmError(e);
        }
        String msg = "Device registered, registration ID=" + this.gcmId;
        Log.i(TAG, msg);
        return this.gcmId;
    }
}
