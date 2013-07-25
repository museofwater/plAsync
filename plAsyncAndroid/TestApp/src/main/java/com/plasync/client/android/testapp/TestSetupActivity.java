package com.plasync.client.android.testapp;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.plasync.client.android.testapp.R;

public class TestSetupActivity extends Activity {

    private static final String TAG = TestSetupActivity.class.getName();

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        String plAsyncServerUrl = getString(R.string.PLASYNC_SERVER_URL_SETTING);
        Intent setupIntent = new Intent();
        ComponentName setupActivityComponent = new ComponentName("com.plasync.client.android.testapp",
                "com.plasync.client.android.AsyncMultiplayerSetupActivity");
        try {
            ActivityInfo info = getPackageManager().getActivityInfo(setupActivityComponent, PackageManager.GET_INTENT_FILTERS);
            Log.d(TAG,info.toString());
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        setupIntent.setComponent(setupActivityComponent);
//        setupIntent.setAction(getString(R.string.SETUP_ASYNC_MULTIPLAYER_SESSION_ACTION));
//        setupIntent.addCategory("android.intent.category.DEFAULT");
        setupIntent.putExtra((getString(R.string.PLASYNC_SERVER_URL_SETTING)), plAsyncServerUrl);
        startActivityForResult(setupIntent,
                getResources().getInteger(R.integer.SETUP_ASYNC_MULTIPLAYER_SESSION_REQUEST_CODE));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
