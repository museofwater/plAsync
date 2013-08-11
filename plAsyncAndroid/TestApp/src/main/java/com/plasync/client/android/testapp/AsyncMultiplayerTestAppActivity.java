package com.plasync.client.android.testapp;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

public class AsyncMultiplayerTestAppActivity extends Activity {

    private static final String TAG = AsyncMultiplayerTestAppActivity.class.getName();
//    private static final String URL = "http://192.168.1.67:9000";
    private static final String URL = "http://192.168.8.72:9000";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_setup);

        // TODO get this via the UI
        String plAsyncServerUrl = URL;
        Intent setupIntent = new Intent();

        // Explicit intent
        ComponentName setupActivityComponent = new ComponentName("com.plasync.client.android.testapp",
                "com.plasync.client.android.AsyncMultiplayerSetupActivity");
        setupIntent.setComponent(setupActivityComponent);

        // Implicit intent
//        setupIntent.setAction(getString(R.string.SETUP_ASYNC_MULTIPLAYER_SESSION_ACTION));
//        setupIntent.addCategory("android.intent.category.DEFAULT");

        setupIntent.putExtra((getString(R.string.PLASYNC_SERVER_URL_SETTING)), plAsyncServerUrl);
        startActivityForResult(setupIntent,
                getResources().getInteger(R.integer.SETUP_ASYNC_MULTIPLAYER_SESSION_REQUEST_CODE));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getResources().getInteger(R.integer.SETUP_ASYNC_MULTIPLAYER_SESSION_RESPONSE_OK_CODE)) {
            String userId = data.getStringExtra(getString(R.string.PLASYNC_USER_ID_SETTING));
            String username = data.getStringExtra(getString(R.string.PLASYNC_USERNAME_SETTING));
        }
        else {
            // async mulitplayer disabled
        }

    }
}
