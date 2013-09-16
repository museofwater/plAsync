package org.plasync.client.android.data.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import org.plasync.client.android.data.contract.AsyncMultiplayerDataContract;
import org.plasync.client.android.data.db.AsyncMultiplayerDatabaseHelper;

/**
 * Created by ericwood on 9/8/13.
 */
public class AsyncMultiplayerDataProvider extends ContentProvider {
    private static final String TAG = AsyncMultiplayerDataProvider.class.getName();

    private static final int USERS = 1;
    private static final int USERS_ID = 2;
    private static final int GCM_SETTINGS = 3;
    private static final int GCM_SETTINGS_ID = 4;

    private static String authority = AsyncMultiplayerDataContract.AUTHORITY;

    // Creates a UriMatcher object.
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(authority, "users", USERS);
        uriMatcher.addURI(authority, "gcmSettings", GCM_SETTINGS);
    }

    private SQLiteDatabase database;
    private AsyncMultiplayerDatabaseHelper dbHelper;


    @Override
    public boolean onCreate() {
        /*
         * Creates a new helper object. This method always returns quickly.
         * Notice that the database itself isn't created or opened
         * until SQLiteOpenHelper.getWritableDatabase is called
         */
        dbHelper = new AsyncMultiplayerDatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Log.i(TAG, "Received query request");
        /*
         * Choose the table to query and a sort order based on the code returned for the incoming
         * URI.
         */
        String tableName;
        switch (uriMatcher.match(uri)) {
            // If the incoming URI was for users
            case 1:
                tableName = AsyncMultiplayerDatabaseHelper.TABLE_USERS;
                break;
            // If the incoming URI was for a specific user
            case 2:
                tableName = AsyncMultiplayerDatabaseHelper.TABLE_USERS;
                selection = selection + "_ID = " + ContentUris.parseId(uri);
                break;
            // If the incoming URI was for gcm settings
            case 3:
                tableName = AsyncMultiplayerDatabaseHelper.TABLE_GCM_SETTINGS;
                break;
            // If the incoming URI was for a specific gcm setting
            case 4:
                tableName = AsyncMultiplayerDatabaseHelper.TABLE_GCM_SETTINGS;
                selection = selection + "_ID = " + ContentUris.parseId(uri);
                break;
            default:
                throw new IllegalArgumentException("Invalid URI " + uri);
                // If the URI is not recognized, you should do some error handling here.
        }

        Cursor cursor = getDatabase().query(tableName, projection, selection, selectionArgs, null, null,
                                   sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        StringBuilder sbMime = new StringBuilder("vnd.android.cursor.");
        boolean isDir;
        String typeName;
        switch (uriMatcher.match(uri)) {
            // If the incoming URI was for users
            case 1:
                typeName = AsyncMultiplayerDatabaseHelper.TABLE_USERS;
                isDir = true;
                break;
            // If the incoming URI was for a specific user
            case 2:
                typeName = AsyncMultiplayerDatabaseHelper.TABLE_USERS;
                isDir = false;
                break;
            // If the incoming URI was for gcm settings
            case 3:
                typeName = AsyncMultiplayerDatabaseHelper.TABLE_GCM_SETTINGS;
                isDir = true;
                break;
            // If the incoming URI was for a specific gcm setting
            case 4:
                typeName = AsyncMultiplayerDatabaseHelper.TABLE_GCM_SETTINGS;
                isDir = false;
                break;
            default:
                throw new IllegalArgumentException("Invalid URI for query" + uri);
                // If the URI is not recognized, you should do some error handling here.
        }
        sbMime.append(isDir ? "dir/vnd." : "item/vnd.");
        sbMime.append(authority);
        sbMime.append(".");
        sbMime.append(typeName);
        return sbMime.toString();
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        String tableName;
        switch (uriMatcher.match(uri)) {
            // If the incoming URI was for users
            case 1:
                tableName = AsyncMultiplayerDatabaseHelper.TABLE_USERS;
                break;
            // If the incoming URI was for gcm settings
            case 3:
                tableName = AsyncMultiplayerDatabaseHelper.TABLE_GCM_SETTINGS;
                break;
            default:
                throw new IllegalArgumentException("Invalid URI for insert" + uri);
                // If the URI is not recognized, you should do some error handling here.
        }
        long insertId = database.insert(tableName, null, contentValues);
        return ContentUris.withAppendedId(uri,insertId);
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        // Not supported
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        // Not supported
        return 0;
    }

    private SQLiteDatabase getDatabase() {
        if (this.database == null) {
            this.database = dbHelper.getWritableDatabase();
        }
        return this.database;
    }
}
