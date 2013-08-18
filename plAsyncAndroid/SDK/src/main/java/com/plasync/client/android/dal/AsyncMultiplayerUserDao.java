package com.plasync.client.android.dal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.plasync.client.android.dal.db.AsyncMultiplayerUserDatabaseHelper;
import com.plasync.client.android.model.AsyncMultiplayerUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ericwood on 8/18/13.
 */
public class AsyncMultiplayerUserDao {
    // Database fields
    private SQLiteDatabase database;
    private AsyncMultiplayerUserDatabaseHelper dbHelper;
    private String[] allColumns = { AsyncMultiplayerUserDatabaseHelper.COLUMN_ID,
                                    AsyncMultiplayerUserDatabaseHelper.COLUMN_SERVER_URL,
                                    AsyncMultiplayerUserDatabaseHelper.COLUMN_USER_ID,
                                    AsyncMultiplayerUserDatabaseHelper.COLUMN_USERNAME};

    public AsyncMultiplayerUserDao(Context context) {
        dbHelper = new AsyncMultiplayerUserDatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public AsyncMultiplayerUser createUser(AsyncMultiplayerUser user) {
        ContentValues values = new ContentValues();
        values.put(AsyncMultiplayerUserDatabaseHelper.COLUMN_SERVER_URL, user.getServerUrl());
        values.put(AsyncMultiplayerUserDatabaseHelper.COLUMN_USER_ID, user.getUserId());
        values.put(AsyncMultiplayerUserDatabaseHelper.COLUMN_USERNAME, user.getUsername());
        long insertId = database.insert(AsyncMultiplayerUserDatabaseHelper.TABLE_USERS, null,
                values);

        // Get the newly inserted value
        Cursor cursor = database.query(AsyncMultiplayerUserDatabaseHelper.TABLE_USERS,
                allColumns, AsyncMultiplayerUserDatabaseHelper.COLUMN_ID + " = " + insertId, null,
                null, null, null);
        cursor.moveToFirst();
        AsyncMultiplayerUser newUser = cursorToUser(cursor);
        cursor.close();
        return newUser;
    }

    public void deleteComment(AsyncMultiplayerUser user) {
        long id = user.getId();
        database.delete(AsyncMultiplayerUserDatabaseHelper.TABLE_USERS,
                        AsyncMultiplayerUserDatabaseHelper.COLUMN_ID + " = " + id, null);
    }

    public List<AsyncMultiplayerUser> getUsersForServerUrl(String serverurl) {
        List<AsyncMultiplayerUser> users = new ArrayList<AsyncMultiplayerUser>();
        // Get the users from the db
        Cursor cursor = database.query(AsyncMultiplayerUserDatabaseHelper.TABLE_USERS,
                                       allColumns,
                                       AsyncMultiplayerUserDatabaseHelper.COLUMN_SERVER_URL+ "=?",
                                       new String[]{serverurl}, null, null, null);

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
                cursor.getColumnIndex(AsyncMultiplayerUserDatabaseHelper.COLUMN_ID));
        AsyncMultiplayerUser user = new AsyncMultiplayerUser(id);
        user.setServerUrl(cursor.getString(
                cursor.getColumnIndex(AsyncMultiplayerUserDatabaseHelper.COLUMN_SERVER_URL)));
        user.setUserId(cursor.getString(
                cursor.getColumnIndex(AsyncMultiplayerUserDatabaseHelper.COLUMN_USER_ID)));
        user.setUsername(cursor.getString(
                cursor.getColumnIndex(AsyncMultiplayerUserDatabaseHelper.COLUMN_USERNAME)));
        return user;
    }
}

