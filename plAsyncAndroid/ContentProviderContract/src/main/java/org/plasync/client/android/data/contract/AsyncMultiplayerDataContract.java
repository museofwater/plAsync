package org.plasync.client.android.data.contract;

/**
 * Created by ericwood on 9/8/13.
 */
public final class AsyncMultiplayerDataContract {
    public static final String AUTHORITY = "org.plasync.client.android.data";
    public static final String BASE_URI = "content://" + AUTHORITY;

    /* Columns shared across multiple tables */
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_SERVER_URL = "server_url";

    public static final String USERS_URI = BASE_URI + "/users";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_USERNAME = "username";

    public static final String GCM_SETTINGS_URI = BASE_URI + "/gcm_settings";
    public static final String COLUMN_SENDER_ID = "sender_id";
    public static final String COLUMN_APP_ID = "app_id";
    public static final String COLUMN_GCM_ID = "gcm_id";
    public static final String COLUMN_RECEIVE_INTENT_NAME = "receive_intent_name";

}
