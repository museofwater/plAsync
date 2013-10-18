package org.plasync.client.android.testapp;

import org.plasync.client.android.AsyncMultiplayerSession;

/**
 * Created by ericwood on 10/14/13.
 */
public class AsyncMultiplayerTestApp {

    private static AsyncMultiplayerSession session;

    public static AsyncMultiplayerSession getSession() {
        return session;
    }

    public static void setSession(AsyncMultiplayerSession session) {
        AsyncMultiplayerTestApp.session = session;
    }
}
