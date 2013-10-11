package org.plasync.client.android;

import android.content.Context;
import android.os.AsyncTask;

import org.plasync.client.android.dal.AsyncMultiplayerUserDao;
import org.plasync.client.android.dal.GcmSettingsDao;
import org.plasync.client.android.gcm.GcmClient;
import org.plasync.client.android.gcm.GcmError;
import org.plasync.client.android.model.App;
import org.plasync.client.android.model.AsyncMultiplayerUser;
import org.plasync.client.android.model.Device;
import org.plasync.client.android.model.DeviceType;
import org.plasync.client.android.model.GcmSettings;
import org.plasync.client.android.model.User;

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

    /**
     * Listener for session events
     */
    private final AsyncMultiplayerSessionListener listener;

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
     * @param listener A listener for session events.  Since the session communicates asynchronously
     *                 with the server, results of session operations will be conveyed via the
     *                 callback method on the listener
     */
    public AsyncMultiplayerSession(Context appContext, AsyncMultiplayerSessionConfig config,
                                   AsyncMultiplayerSessionListener listener) {
        this.appContext = appContext;
        this.config = config;
        this.listener = listener;
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
     */
    public void init() {
        // Initialize in background
        new AsyncTask<Void, Void, Void>() {
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
                    listener.onInitComplete();
                }
                else {
                    listener.onInitError(this.error);
                }
            }
        }.execute();
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

    public interface AsyncMultiplayerSessionListener {
        void onInitComplete();
        void onInitError(AsyncMultiplayerSessionError error);
    }
}
