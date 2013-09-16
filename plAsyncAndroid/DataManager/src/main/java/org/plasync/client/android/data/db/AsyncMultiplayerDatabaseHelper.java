package org.plasync.client.android.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.plasync.client.android.data.contract.AsyncMultiplayerDataContract;

/**
 * Created by ericwood on 9/8/13.
 */
public class AsyncMultiplayerDatabaseHelper extends SQLiteOpenHelper {
    private static final String TAG = AsyncMultiplayerDatabaseHelper.class.getName();

    private static final String DATABASE_NAME = "asyncMultiplayer.db";
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_USERS = "users";
    public static final String TABLE_GCM_SETTINGS = "gcm_settings";

    // Database creation sql statement
    private static final String DATABASE_CREATE =
            "create table " + TABLE_USERS + "(" +
            AsyncMultiplayerDataContract.COLUMN_ID + " integer primary key autoincrement, " +
            AsyncMultiplayerDataContract.COLUMN_SERVER_URL + " text not null, " +
            AsyncMultiplayerDataContract.COLUMN_USER_ID + " text not null, " +
            AsyncMultiplayerDataContract.COLUMN_USERNAME + " text not null" +
            ");"
            + "create table " + TABLE_GCM_SETTINGS + "(" +
            AsyncMultiplayerDataContract.COLUMN_ID + " integer primary key autoincrement, " +
            AsyncMultiplayerDataContract.COLUMN_SERVER_URL + " text not null, " +
            AsyncMultiplayerDataContract.COLUMN_APP_ID + " text not null, " +
            AsyncMultiplayerDataContract.COLUMN_GCM_ID + " text not null, " +
            AsyncMultiplayerDataContract.COLUMN_RECEIVE_INTENT_NAME + " text not null, " +
            ");";


    public AsyncMultiplayerDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        Log.d(TAG, "creating database using SQL: " + DATABASE_CREATE);
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int i, int i2) {
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS + ";" +
                         "DROP TABLE IF EXISTS " + TABLE_GCM_SETTINGS + ";");
        onCreate(database);
    }
}
