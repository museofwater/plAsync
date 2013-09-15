package com.plasync.client.android.dal;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.plasync.client.android.data.contract.AsyncMultiplayerDataContract;

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

    private static final String[] ALL_COLUMNS = {COLUMN_ID, COLUMN_SERVER_URL, COLUMN_APP_ID, COLUMN_GCM_ID};
    private static final String EMPTY_SELECTION = "";
    private static final String[] EMPTY_SELECTION_ARGS = {};
    private static final String SERVER_URL_SELECTION = COLUMN_SERVER_URL + " = ?";

    private ContentResolver contentResolver;

    public GcmSettingsDao(Context context) {
        contentResolver = context.getContentResolver();
    }

    public GcmSettings createGcmSettings(GcmSettings gcmSettings) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_SERVER_URL, gcmSettings.getServerUrl());
        values.put(COLUMN_GCM_ID, gcmSettings.getGcmId());
        values.put(COLUMN_USERNAME, user.getUsername());
        Uri resourceUri = contentResolver.insert(Uri.parse(contentUri),values);

        // Get the newly inserted value
        Cursor cursor = contentResolver.query(resourceUri, ALL_COLUMNS, EMPTY_SELECTION,
                EMPTY_SELECTION_ARGS, null);
        cursor.moveToFirst();
        AsyncMultiplayerUser newUser = cursorToUser(cursor);
        cursor.close();
        return newUser;
    }
}
