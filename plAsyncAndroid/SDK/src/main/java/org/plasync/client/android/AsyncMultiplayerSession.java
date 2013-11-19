package org.plasync.client.android;

import android.content.Context;
import android.os.AsyncTask;

import org.plasync.client.android.dal.GcmSettingsDao;
import org.plasync.client.android.gcm.GcmClient;
import org.plasync.client.android.gcm.GcmError;
import org.plasync.client.android.model.App;
import org.plasync.client.android.model.DeviceType;
import org.plasync.client.android.model.FriendRequest;
import org.plasync.client.android.model.GcmSettings;
import org.plasync.client.android.model.User;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ericwood
 * Date: 7/20/13
 * Time: 12:53 PM
 * To change this template use File | Settings | File Templates.
 */
public class AsyncMultiplayerSession {

    /**
     * The application context for the app.
     *
     * Used to do things that require a context (i.e. binding services)
     */
    private Context appContext = null;

    private AsyncMultiplayerSessionConfig config;

    private GcmSettingsDao gcmSettingsDao;

    /**
     * The asyncMultiplayerClient to communicate with the plAsync Server
     */
    private final AsyncMultiplayerClient asyncMultiplayerClient;

    private GcmClient gcmClient;


    /**
     * Creates a new AsyncMultiplayerSession
     *
     * @param appContext The application context
     * @param config The configuration for the session
     */
    public AsyncMultiplayerSession(Context appContext, AsyncMultiplayerSessionConfig config) {
        this.appContext = appContext;
        this.config = config;
        this.asyncMultiplayerClient = new AsyncMultiplayerClient(config.getPlasyncApiKey(),config.getPlasyncServerUrl());
        this.gcmSettingsDao = new GcmSettingsDao(appContext);

    }

    /**
     * Initialize the session
     *
     * This will ensure that the app and device are registered with GCM and with the plAsync server.
     * This is an async call.  The listener's onInitComplete will be invoked if initialization is
     * successful, otherwise the listener's onInitError will be invoked.
     * complete
     * @param callback A listener for session init events.  Since the session communicates asynchronously
     *                 with the server, results of the init will be conveyed via the
     *                 callback method on the listener
     */
    public void init(SessionInitListener callback) {
        // Initialize in background
        new AsyncTask<Void, Void, Void>() {
            SessionInitListener callback;
            AsyncMultiplayerSessionError error;

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    AsyncMultiplayerSession.this.gcmClient =
                            new GcmClient(AsyncMultiplayerSession.this.appContext);
                    String sGcmId = getLocalGcmId();
                    if (sGcmId == null) {
                        // Get the id from GCM
                        sGcmId = AsyncMultiplayerSession.this.gcmClient.getGcmId(getSenderId());

                        // Tell the server about the user, app and it's GCM id
                        registerApp(sGcmId);

                        // Store the GCM locally
                        storeGcmSettings(sGcmId,config.getReceiveIntentName());
                    }
                }
                catch (AsyncMultiplayerSessionError error) {
                    this.error = error;
                }
                catch (GcmError gcmError) {
                    this.error = new AsyncMultiplayerSessionError("Failed to register with message service");
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (error == null) {
                    callback.onInitComplete();
                }
                else {
                    callback.onInitError(this.error);
                }
            }

            public AsyncTask<Void, Void, Void> setCallback(SessionInitListener callback) {
                this.callback = callback;
                return this;
            }
        }.setCallback(callback).execute();
    }

    /**
     * Searches for all users of the application with the app context passed in in config
     * @param query The string to search for in the username
     * @param callback A listener for search events.  Since the session communicates asynchronously
     *                 with the server, results of search operations will be conveyed via the
     *                 callback method on the listener
     */
    public void searchUsers(String query, SearchListener callback) {
        // Search in background
        new AsyncTask<String, Void, List<User>>() {
            SearchListener callback;
            AsyncMultiplayerSessionError error;

            @Override
            protected List<User> doInBackground(String... params) {
                try {
                    return asyncMultiplayerClient.searchUsers(appContext.getPackageName(), params[0]);
                }
                catch (AsyncMultiplayerSessionError error) {
                    this.error = error;
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<User> users) {
                if (error == null) {
                    callback.onSearchComplete(users);
                }
                else {
                    callback.onSearchError(this.error);
                }
            }

            public AsyncTask<String, Void, List<User>> setCallback(SearchListener callback) {
                this.callback = callback;
                return this;
            }
        }.setCallback(callback).execute(query);
    }

    /**
     * Retrieves all friend requests (pending, accepted, and declined) for the specified user for
     * the application with the app context
     * passed in in the config
     * @param user The user to find friend requests for
     * @param callback A listener for the results.  Since the session communicates asynchronously
     *                 with the server, results of getting friend requests  will be conveyed via the
     *                 callback method on the listener
     */
    public void getFriendRequests(User user, GetFriendRequestsListener callback) {
        // Search in background
        new AsyncTask<User, Void, List<FriendRequest>>() {
            GetFriendRequestsListener callback;
            AsyncMultiplayerSessionError error;

            @Override
            protected List<FriendRequest> doInBackground(User... params) {
                try {
                    return asyncMultiplayerClient.getFriendRequests(appContext.getPackageName(), params[0]);
                }
                catch (AsyncMultiplayerSessionError error) {
                    this.error = error;
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<FriendRequest> friendRequests) {
                if (error == null) {
                    callback.onGetFriendRequestsComplete(friendRequests);
                }
                else {
                    callback.onGetFriendRequestsError(this.error);
                }
            }

            public AsyncTask<User, Void, List<FriendRequest>> setCallback(GetFriendRequestsListener callback) {
                this.callback = callback;
                return this;
            }
        }.setCallback(callback).execute(user);
    }

    /**
     * Gets the GCM sender ID from the server
     * @return
     */
    private String getSenderId() throws AsyncMultiplayerSessionError {
        return asyncMultiplayerClient.getSenderId();
    }

    private String getLocalGcmId() {
        GcmSettings gcmSettings = gcmSettingsDao.getGcmSettingsForApp(appContext.getPackageName());
        return gcmSettings != null ? gcmSettings.getGcmId() : null;
    }

    private void registerApp(String sGcmId) throws AsyncMultiplayerSessionError {
        App app = new App(config.getUser(), appContext.getPackageName(), DeviceType.ANDROID, sGcmId);
        asyncMultiplayerClient.addApp(app);
    }

    private void storeGcmSettings(String sGcmId, String receiveIntentName) {
        GcmSettings gcmSettings = new GcmSettings(config.getPlasyncServerUrl(),appContext.getPackageName(),
                                                  sGcmId,receiveIntentName);
        gcmSettingsDao.createGcmSettings(gcmSettings);
    }

    public interface SessionInitListener {
        void onInitComplete();
        void onInitError(AsyncMultiplayerSessionError error);
    }

    public interface SearchListener {
        void onSearchComplete(List<User> users);
        void onSearchError(AsyncMultiplayerSessionError error);
    }

    public interface GetFriendRequestsListener {
        void onGetFriendRequestsComplete(List<FriendRequest> friendRequests);
        void onGetFriendRequestsError(AsyncMultiplayerSessionError error);
    }
}
