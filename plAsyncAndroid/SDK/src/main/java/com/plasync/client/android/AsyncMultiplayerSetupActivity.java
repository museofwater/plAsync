package com.plasync.client.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.plasync.client.android.dal.AsyncMultiplayerUserDao;
import com.plasync.client.android.model.AsyncMultiplayerUser;
import com.plasync.client.android.util.NetworkUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;
import java.util.Scanner;

/**
 * Created by ericwood on 7/22/13.
 */
public class AsyncMultiplayerSetupActivity extends Activity {

    private static final String TAG = AsyncMultiplayerSetupActivity.class.getName();
    public static final String UTF_8 = "UTF-8";

    private WebView wvSignin;
    private String plAsyncServerUrl;
    private AsyncMultiplayerUserDao userDao;
    private ProgressDialog progressLoadUrl;
    private boolean progressIsShowing = false;
    private SigninWebViewClient webViewClient;

    public void onCreate(Bundle savedInstanceState) {
        userDao = new AsyncMultiplayerUserDao(this);
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();

        setPlAsyncServerUrl(intent.getStringExtra(getString(R.string.PLASYNC_SERVER_URL_SETTING)));

        // Try to get the user from local storage
        AsyncMultiplayerUser user = getLocalUser(plAsyncServerUrl);

        if (user != null) {
            setSigninResult(user);
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
        // TODO add support for multiple users on a device
        userDao.open();
        List<AsyncMultiplayerUser> users = userDao.getUsersForServerUrl(url);
        if (!users.isEmpty()) {
            return users.get(0);
        }
        else {
            return null;
        }
    }

    private void registerUser() {
        webViewClient = new SigninWebViewClient(getResources().getInteger(R.integer.timeout));
        setContentView(R.layout.setup);
        wvSignin = (WebView)findViewById(R.id.wvSignin);
        wvSignin.getSettings().setJavaScriptEnabled(true);
        wvSignin.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        wvSignin.setWebViewClient(webViewClient);
        loadUrl(getUrl(getString(R.string.SIGNIN_URL)));
    }

    private String getUrl(String subUrl) {
        return plAsyncServerUrl + subUrl;
    }

    private void setPlAsyncServerUrl(String url) {
        // make sure url ends in a trailing "/"
        plAsyncServerUrl = url.endsWith("/") ? url : url + "/";
    }

    private void loadUrl(String url) {
        if (!NetworkUtil.checkNetwork(this)) {
            setSigninFailureResult();
        }
        // Show progress
        progressIsShowing = true;
        progressLoadUrl =
                ProgressDialog.show(this, getString(R.string.CONNECTING_TITLE),
                        getString(R.string.CONNECTING_MSG));
        webViewClient.prepareToLoadUrl();
        wvSignin.loadUrl(url);
    }

    private void setSigninFailureResult() {
        setResult(getResources().getInteger(R.integer.SETUP_ASYNC_MULTIPLAYER_SESSION_RESPONSE_FAILED_CODE));
        // Close the database
        userDao.close();
        finish();
    }

    private void setSigninResult(AsyncMultiplayerUser user) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(getString(R.string.PLASYNC_USERNAME_SETTING),user.getUsername());
        resultIntent.putExtra(getString(R.string.PLASYNC_USER_ID_SETTING),user.getUserId());
        setResult(getResources().getInteger(R.integer.SETUP_ASYNC_MULTIPLAYER_SESSION_RESPONSE_OK_CODE),
                resultIntent);
        // Close the database
        userDao.close();
        finish();
    }

    private void setSigninResult(String username, String userId) {
        // Store user in database
        setSigninResult(storeUser(userId, username));
    }

    private AsyncMultiplayerUser storeUser(String userId, String username) {
        AsyncMultiplayerUser newUser = new AsyncMultiplayerUser();
        newUser.setServerUrl(plAsyncServerUrl);
        newUser.setUserId(userId);
        newUser.setUsername(username);
        return userDao.createUser(newUser);
    }

    private class SigninWebViewClient extends WebViewClient {
        /**
         * Timeout for page load in seconds
         */
        private int timeout;
        private String urlLoading;

        boolean pageLoaded = false;

        // Flag to instruct the client to ignore callbacks after an error
        boolean hasError = false;

        Handler timeoutHandler;
        private AlertDialog alertDialog;

        private SigninWebViewClient(int timeout) {
            this.timeout = timeout;
            timeoutHandler = new Handler();
        }

        // Called by activity before requesting load of a url
        private void prepareToLoadUrl() {
           this.hasError = false;
           this.pageLoaded = true;
           this.urlLoading = null;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            if (hasError) {
                return;
            }
            urlLoading = url;
            // timeout has expired if this flag is still set when the message is handled
            pageLoaded = false;
            Runnable run = new Runnable() {
                public void run() {
                    // Do nothing if we already have an error
                    if (hasError) {
                        return;
                    }

                    // Dismiss any current alerts and progress
                    dismissProgress();
                    dismissErrorAlert();
                    if (!pageLoaded) {
                        showTimeoutAlert();
                    }
                }
            };
            timeoutHandler.postDelayed(run, this.timeout*1000);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            // Ignore future callbacks because the page load has failed
            hasError = true;
            dismissProgress();
            showServerErrorAlert();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            if (hasError) {
                return;
            }
            pageLoaded = true;
            dismissProgress();
            dismissErrorAlert();
            urlLoading = null;
            if (url.endsWith(getString(R.string.CLOSE_URL))) {
                CookieSyncManager.getInstance().sync();
                // Get the cookie from cookie jar.
                String cookie = CookieManager.getInstance().getCookie(url);
                if (cookie == null) {
                    setSigninFailureResult();
                }

                // Both must be found in cookie for signin sucess
                String username = null;
                String userId = null;
//                // Cookie is a string like NAME=VALUE [; NAME=VALUE]
//                String[] pairs = cookie.split(";");
//                // Loop over all cookies
//                for (int i = 0; i < pairs.length; ++i) {
//                    String[] parts = pairs[i].split("=", 2);
//                    String cookieName = parts[0].trim();
//                    String cookieValue = parts[1].trim();
//                    if (parts.length == 2) {
//                        if (cookieName.equalsIgnoreCase(getString(R.string.PLASYNC_USERNAME_COOKIE))) {
//                            username = cookieValue;
//                        }
//                        else if (cookieName.equalsIgnoreCase(getString(R.string.PLASYNC_USERID_COOKIE))) {
//                            userId = cookieValue;
//                        }
//                    }
//                }
                Scanner cookieScanner = new Scanner(cookie);
                cookieScanner.useDelimiter(";");
                while (cookieScanner.hasNext()) {
                    String cookieItem = cookieScanner.next();
                    String cookieName = null;
                    String cookieValue = null;
                    Scanner cookieItemScanner = new Scanner(cookieItem);
                    cookieItemScanner.useDelimiter("=");
                    if (cookieItemScanner.hasNext()) {
                        cookieName = cookieItemScanner.next().trim();
                    }
                    if (cookieItemScanner.hasNext()) {
                        cookieValue = cookieItemScanner.next().trim();
                        // URLDecode the cookie value
                        try {
                            cookieValue = URLDecoder.decode(cookieValue, UTF_8);
                        }
                        catch (UnsupportedEncodingException e) {
                            Log.e(TAG, "Error decoding cookie value", e);
                        }
                    }

                    if (cookieName != null && cookieValue != null &&
                        cookieName.equalsIgnoreCase(getString(R.string.PLASYNC_USERNAME_COOKIE))) {
                        username = cookieValue;
                    }
                    if (cookieName != null && cookieValue != null &&
                            cookieName.equalsIgnoreCase(getString(R.string.PLASYNC_USERID_COOKIE))) {
                        userId = cookieValue;
                    }
                }

                // Check for both cookies
                if (username == null || userId == null) {
                    setSigninFailureResult();
                }
                else {
                    setSigninResult(username,userId);
                }
            }
        }

        private void showTimeoutAlert() {
            showErrorAlert(R.string.TIMEOUT_TITLE, R.string.TIMEOUT_MSG);
        }

        private void showServerErrorAlert() {
            showErrorAlert(R.string.SERVER_ERROR_TITLE,R.string.SERVER_ERROR_MSG);
        }

        private void showErrorAlert(int titleResource, int messageResource) {
            AlertDialog.Builder builder = new AlertDialog.Builder(AsyncMultiplayerSetupActivity.this);
            // Add the buttons
            builder.setTitle(titleResource)
                    .setMessage(messageResource)
                    .setPositiveButton(R.string.RETRY, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Try to load url again
                            loadUrl(urlLoading);
                            dialog.dismiss();
                        }
                    });
            builder.setNegativeButton(R.string.CANCEL, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                    setSigninFailureResult();
                    dialog.cancel();
                }
            });

            // Create the AlertDialog
            alertDialog = builder.create();
            alertDialog.show();
        }

        private void dismissProgress() {
            if (progressLoadUrl != null && progressLoadUrl.isShowing()) {
                progressLoadUrl.dismiss();
            }
        }

        private void dismissErrorAlert() {
            if (alertDialog != null && alertDialog.isShowing()) {
                alertDialog.dismiss();
            }
        }
    }




}