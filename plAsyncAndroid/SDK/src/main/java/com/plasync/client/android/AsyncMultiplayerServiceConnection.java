package com.plasync.client.android;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

/**
 * Created with IntelliJ IDEA.
 * User: ericwood
 * Date: 7/20/13
 * Time: 1:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class AsyncMultiplayerServiceConnection implements ServiceConnection {

    private boolean bound = false;
    private IBinder service = null;

    public void onServiceConnected(ComponentName className, IBinder service) {
        // This is called when the connection with the service has been
        // established, giving us the object we can use to
        // interact with the service.  We are communicating with the
        // service using a Messenger, so here we get a client-side
        // representation of that from the raw IBinder object.
        this.service = service;
        bound = true;
    }

    public void onServiceDisconnected(ComponentName className) {
        // This is called when the connection with the service has been
        // unexpectedly disconnected -- that is, its process crashed.
        this.service = null;
        bound = false;
    }
}
