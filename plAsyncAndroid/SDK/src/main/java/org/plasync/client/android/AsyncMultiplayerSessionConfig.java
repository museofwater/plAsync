package org.plasync.client.android;

import org.plasync.client.android.model.User;

/**
 * Created by ericwood on 10/2/13.
 */
public interface AsyncMultiplayerSessionConfig {

    /**
     * Get the base URL of the plAsyncServer that this session is connected to
     */
    String getPlasyncServerUrl();


    /**
     * Get the plAsync API Key for using the service.
     *
     * This may be required by some servers.  Can be null, but that may cause the server to reject the request
     */
    String getPlasyncApiKey();

    /**
     * Get the name of the intent service that should be started to receive
     * @return
     */
    String getReceiveIntentName();

    /**
     * Get the user for the session
     */
    User getUser();




}
