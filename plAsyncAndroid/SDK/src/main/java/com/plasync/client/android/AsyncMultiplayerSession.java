package com.plasync.client.android;

import android.content.Context;

/**
 * Created with IntelliJ IDEA.
 * User: ericwood
 * Date: 7/20/13
 * Time: 12:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class AsyncMultiplayerSession {

    /**
     * The base URL of the plAsyncServer that this session is connected to
     */
    String plAsyncServerUrl;

    /**
     * The plAsync API Key for using the service.
     *
     * This may be required by some servers.  Can be null, but that may cause the server to reject the request
     */
    String plAsyncApiKey;

    /**
     * The application context for the app.
     *
     * Used to do things that require a context (i.e. binding services)
     */
    Context appContext = null;

//    /** Messenger for communicating with the service. */
//    Messenger mService = null;
//
//    AsyncMultiplayerServiceConnection connection;

    public AsyncMultiplayerSession(Context appContext, String plAsyncServerUrl, String plAsyncApiKey) {
        this.appContext = appContext;
        this.plAsyncServerUrl = plAsyncServerUrl;
        this.plAsyncApiKey = plAsyncApiKey;
    }

    /** Initializes the session which involves the following
     *
     * 1.  Detecting the existence of the plAsync service and prompting the user to install if it does not exist
     * 2.  Requesting the user id and username from the service
     *
     * @throws SessionInitializationFailedException
     */
//    public void init() throws SessionInitializationFailedException {
//        AsyncMultiplayerUser user = getLocalUser(plAsyncServerUrl);
//        if (user == null) {
//            user = registerUser();
//        }
//    }




}
