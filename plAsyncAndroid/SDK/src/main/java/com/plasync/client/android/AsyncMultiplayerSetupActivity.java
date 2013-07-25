package com.plasync.client.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by ericwood on 7/22/13.
 */
public class AsyncMultiplayerSetupActivity extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        String plAsyncUrl = intent.getStringExtra(getString(R.string.PLASYNC_SERVER_URL_SETTING));

        // Try to get the user from local storage
        AsyncMultiplayerUser user = getLocalUser(plAsyncUrl);

        if (user != null) {
            Intent result = new Intent();
            result.putExtra(getString(R.string.PLASYNC_USER_ID_SETTING), user.getId());
            result.putExtra(getString(R.string.PLASYNC_USERNAME_SETTING), user.getUsername());
            setResult(getResources().getInteger(
                    R.integer.SETUP_ASYNC_MULTIPLAYER_SESSION_RESPONSE_OK_CODE), result);
            finish();
        }
    }

    /**
     * Get the local device user information from device storage for the specified server url
     * @param url The url of the server, with which this user should be registered
     * @return The user or null if one is not stored locally for this URL
     */
    private AsyncMultiplayerUser getLocalUser(String url) {
        return null;  //To change body of created methods use File | Settings | File Templates.
    }

//    private AsyncMultiplayerUser registerUser() {
//
//    }


}