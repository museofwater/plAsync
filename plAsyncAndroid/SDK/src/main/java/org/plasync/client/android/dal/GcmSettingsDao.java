package org.plasync.client.android.dal;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import org.plasync.client.android.data.contract.AsyncMultiplayerDataContract;

import org.plasync.client.android.model.GcmSettings;

/**
 * Created by ericwood on 9/12/13.
 */
public class GcmSettingsDao {

    // URI for content.
    private static final String contentUri = AsyncMultiplayerDataContract.GCM_SETTINGS_URI;
    private static final String COLUMN_ID = AsyncMultiplayerDataContract.COLUMN_ID;
    private static final String COLUMN_SERVER_URL = AsyncMultiplayerDataContract.COLUMN_SERVER_URL;
    private static final String COLUMN_APP_ID = AsyncMultiplayerDataContract.COLUMN_APP_ID;
    private static final String COLUMN_GCM_ID = AsyncMultiplayerDataContract.COLUMN_GCM_ID;
    private static final String COLUMN_RECEIVE_INTENT_NAME =
            AsyncMultiplayerDataContract.COLUMN_RECEIVE_INTENT_NAME;

    private static final String[] ALL_COLUMNS = {COLUMN_ID, COLUMN_SERVER_URL, COLUMN_APP_ID,
                                                 COLUMN_GCM_ID, COLUMN_RECEIVE_INTENT_NAME};
    private static final String EMPTY_SELECTION = "";
    private static final String[] EMPTY_SELECTION_ARGS = {};
    private static final String APP_ID_SELECTION = COLUMN_APP_ID + " = ?";

    private ContentResolver contentResolver;

    public GcmSettingsDao(Context context) {
        contentResolver = context.getContentResolver();
    }

    public GcmSettings createGcmSettings(GcmSettings gcmSettings) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_SERVER_URL, gcmSettings.getServerUrl());
        values.put(COLUMN_APP_ID, gcmSettings.getAppId());
        values.put(COLUMN_GCM_ID, gcmSettings.getGcmId());
        values.put(COLUMN_RECEIVE_INTENT_NAME, gcmSettings.getReceiveIntentName());
        Uri resourceUri = contentResolver.insert(Uri.parse(contentUri),values);

        // Get the newly inserted value
        Cursor cursor = contentResolver.query(resourceUri, ALL_COLUMNS, EMPTY_SELECTION,
                EMPTY_SELECTION_ARGS, null);
        cursor.moveToFirst();
        GcmSettings newSettings = cursorToGcmSettings(cursor);
        cursor.close();
        return newSettings;
    }

    /**
     * Gets the settings for the specified app, identified by package name
     * @param appId The package name for the app
     * @return
     */
    public GcmSettings getGcmSettingsForApp(String appId) {
        // Get the users from the db
        Cursor cursor = contentResolver.query(Uri.parse(contentUri), ALL_COLUMNS, APP_ID_SELECTION,
                new String[]{appId}, null);

        // Loop over all users returned and add them to the return list
        cursor.moveToFirst();
        if (!cursor.isAfterLast()) {
            GcmSettings gcmSettings = cursorToGcmSettings(cursor);
            return gcmSettings;
        }
        // Make sure to close the cursor
        cursor.close();
        return null;
    }

    private GcmSettings cursorToGcmSettings(Cursor cursor) {
        long id = cursor.getLong(
                cursor.getColumnIndex(COLUMN_ID));
        GcmSettings gcmSettings = new GcmSettings(id);
        gcmSettings.setServerUrl(cursor.getString(
                cursor.getColumnIndex(COLUMN_SERVER_URL)));
        gcmSettings.setAppId(cursor.getString(
                cursor.getColumnIndex(COLUMN_APP_ID)));
        gcmSettings.setGcmId(cursor.getString(
                cursor.getColumnIndex(COLUMN_GCM_ID)));
        return gcmSettings;
    }
}
