package com.plasync.client.android.testapp;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.plasync.client.android.testapp.fragments.ServerUrlDialogFragment;

public class AsyncMultiplayerTestAppActivity extends FragmentActivity {

    private static final String TAG = AsyncMultiplayerTestAppActivity.class.getName();
//    private static final String URL = "http://192.168.1.67:9000";
//    private static final String URL = "http://192.168.8.72:9000";

    private TextView tvUsername;
    
    private String username = null;
    private String userId = null;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_app_main);

        tvUsername = (TextView) findViewById(R.id.tvUsername);
        
        if (username == null || userId == null) {
            setupAsyncSession();
        }   
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getResources().getInteger(R.integer.SETUP_ASYNC_MULTIPLAYER_SESSION_RESPONSE_OK_CODE)) {
            userId = data.getStringExtra(getString(R.string.PLASYNC_USER_ID_SETTING));
            username = data.getStringExtra(getString(R.string.PLASYNC_USERNAME_SETTING));
            tvUsername.setText(username);
        }
        else {
            // async mulitplayer disabled
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.test_app_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menuSetServerUrl:
                setupAsyncSession();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setupAsyncSession() {
        getUrlFromUser();
    }

    private void setupAsyncSession(String url) {
        String plAsyncServerUrl = url;
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

    private void getUrlFromUser() {
        FragmentManager fm = getSupportFragmentManager();
        ServerUrlDialogFragment serverUrlDialog = new ServerUrlDialogFragment();
        serverUrlDialog.setListener(new ServerUrlDialogFragment.ServerUrlDialogListener() {
            @Override
            public void onFinishEditDialog(String inputText) {
                setupAsyncSession(inputText);
            }
        });
        serverUrlDialog.show(fm, "fragment_server_url");
    }


}
