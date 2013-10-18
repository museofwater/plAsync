package org.plasync.client.android.testapp;

import org.plasync.client.android.AsyncMultiplayerSessionConfig;
import org.plasync.client.android.model.User;

/**
 * Created by ericwood on 10/9/13.
 */
public class AsyncMultiplayerTestAppSessionConfig implements AsyncMultiplayerSessionConfig {

    private static final String API_KEY = null;
    private static final String receiveIntentName = AsyncMultiplayerTestAppMessageReceiver.class.getName();

    private String serverUrl;
    private User user;

    @Override
    public String getPlasyncServerUrl() {
        return this.serverUrl;
    }

    @Override
    public String getPlasyncApiKey() {
        return API_KEY;
    }

    @Override
    public String getReceiveIntentName() {
        return receiveIntentName;
    }

    @Override
    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }
}
