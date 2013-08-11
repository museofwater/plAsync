package com.plasync.client.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by ericwood on 7/22/13.
 */
public class AsyncMultiplayerSetupActivity extends Activity {

    private static final String TAG = AsyncMultiplayerSetupActivity.class.getName();

    private WebView wvSignin;
    private String plAsyncServerUrl;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        setPlAsyncServerUrl(intent.getStringExtra(getString(R.string.PLASYNC_SERVER_URL_SETTING)));

        // Try to get the user from local storage
        AsyncMultiplayerUser user = getLocalUser(plAsyncServerUrl);

        if (user != null) {
            Intent result = new Intent();
            result.putExtra(getString(R.string.PLASYNC_USER_ID_SETTING), user.getId());
            result.putExtra(getString(R.string.PLASYNC_USERNAME_SETTING), user.getUsername());
            setResult(getResources().getInteger(
                    R.integer.SETUP_ASYNC_MULTIPLAYER_SESSION_RESPONSE_OK_CODE), result);
            finish();
        }
        else {
            registerUser();
        }
    }

    /**
     * Get the local device user information from device storage for the specified server url
     * @param url The url of the server, with which this user should be registered
     * @return The user or null if one is not stored locally for this URL
     */
    private AsyncMultiplayerUser getLocalUser(String url) {
//        return new AsyncMultiplayerUser("id", "username");
        return null;
    }

    private AsyncMultiplayerUser registerUser() {
        setContentView(R.layout.setup);
        wvSignin = (WebView)findViewById(R.id.wvSignin);
        wvSignin.getSettings().setJavaScriptEnabled(true);
        wvSignin.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        wvSignin.setWebViewClient(new SigninWebViewClient());
        loadUrl(getString(R.string.SIGNIN_URL));

        return null;
    }

    private void setPlAsyncServerUrl(String url) {
        // make sure url ends in a trailing "/"
        plAsyncServerUrl = url.endsWith("/") ? url : url + "/";
    }

    private void loadUrl(String subUrl) {
        wvSignin.loadUrl(plAsyncServerUrl + subUrl);
    }

    private class SigninWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            if (url.endsWith(getString(R.string.CLOSE_URL))) {
                setResult(getResources().getInteger(R.integer.SETUP_ASYNC_MULTIPLAYER_SESSION_RESPONSE_FAILED_CODE));
                finish();
            }
//            CookieSyncManager.getInstance().sync();
//            // Get the cookie from cookie jar.
//            String cookie = CookieManager.getInstance().getCookie(url);
//            if (cookie == null) {
//                return;
//            }
//            // Cookie is a string like NAME=VALUE [; NAME=VALUE]
//            String[] pairs = cookie.split(";");
//            for (int i = 0; i < pairs.length; ++i) {
//                String[] parts = pairs[i].split("=", 2);
//                // If token is found, return it to the calling activity.
//                if (parts.length == 2 &&
//                        parts[0].equalsIgnoreCase("oauth_token")) {
//                    Intent result = new Intent();
//                    result.putExtra("token", parts[1]);
//                    setResult(RESULT_OK, result);
//                    finish();
//                }
//            }
        }
    }


}