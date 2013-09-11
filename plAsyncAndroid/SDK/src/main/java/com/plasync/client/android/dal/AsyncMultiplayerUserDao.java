package com.plasync.client.android.dal;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.plasync.client.android.data.contract.AsyncMultiplayerDataContract;
import com.plasync.client.android.model.AsyncMultiplayerUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ericwood on 8/18/13.
 */
public class AsyncMultiplayerUserDao {

    // URI for content.
    private static final String contentUri = AsyncMultiplayerDataContract.USERS_URI;
    private static final String COLUMN_ID = AsyncMultiplayerDataContract.COLUMN_ID;
    private static final String COLUMN_SERVER_URL = AsyncMultiplayerDataContract.COLUMN_SERVER_URL;
    private static final String COLUMN_USER_ID = AsyncMultiplayerDataContract.COLUMN_USER_ID;
    private static final String COLUMN_USERNAME = AsyncMultiplayerDataContract.COLUMN_USERNAME;

    private static final String[] ALL_COLUMNS = {COLUMN_SERVER_URL, COLUMN_USER_ID, COLUMN_USERNAME};
    private static final String EMPTY_SELECTION = "";
    private static final String[] EMPTY_SELECTION_ARGS = {};
    private static final String SERVER_URL_SELECTION = COLUMN_SERVER_URL + " = ?";

    private ContentResolver contentResolver;


    public AsyncMultiplayerUserDao(Context context) {
        contentResolver = context.getContentResolver();
    }

    public AsyncMultiplayerUser createUser(AsyncMultiplayerUser user) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_SERVER_URL, user.getServerUrl());
        values.put(COLUMN_USER_ID, user.getUserId());
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

    public List<AsyncMultiplayerUser> getUsersForServerUrl(String serverurl) {
        List<AsyncMultiplayerUser> users = new ArrayList<AsyncMultiplayerUser>();
        // Get the users from the db
        Cursor cursor = contentResolver.query(Uri.parse(contentUri), ALL_COLUMNS, SERVER_URL_SELECTION,
                                              new String[]{serverurl}, null);

        // Loop over all users returned and add them to the return list
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            AsyncMultiplayerUser user = cursorToUser(cursor);
            users.add(user);
            cursor.moveToNext();
        }
        // Make sure to close the cursor
        cursor.close();
        return users;
    }

    private AsyncMultiplayerUser cursorToUser(Cursor cursor) {
        long id = cursor.getLong(
                cursor.getColumnIndex(COLUMN_ID));
        AsyncMultiplayerUser user = new AsyncMultiplayerUser(id);
        user.setServerUrl(cursor.getString(
                cursor.getColumnIndex(COLUMN_SERVER_URL)));
        user.setUserId(cursor.getString(
                cursor.getColumnIndex(COLUMN_USER_ID)));
        user.setUsername(cursor.getString(
                cursor.getColumnIndex(COLUMN_USERNAME)));
        return user;
    }
}

