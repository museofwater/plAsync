package com.plasync.client.android.dal.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ericwood on 8/16/13.
 */
public class AsyncMultiplayerUserDatabaseHelper extends SQLiteOpenHelper {

    public static final String TABLE_USERS = "users";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_SERVER_URL = "server_url";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_USERNAME = "username";

    private static final String DATABASE_NAME = "com.plasync.client.users.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_USERS + "(" +
                COLUMN_ID + " integer primary key autoincrement, " +
                COLUMN_SERVER_URL + " text not null, " +
                COLUMN_USER_ID + " text not null, " +
                COLUMN_USERNAME + " text not null" +
            ");";

    public AsyncMultiplayerUserDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int i, int i2) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(database);
    }
}
