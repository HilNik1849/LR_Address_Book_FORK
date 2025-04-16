package com.example.myapplication;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import static com.example.myapplication.DatabaseDescription.AUTHORITY;

public class AddressBookContentProvider extends ContentProvider {
    private AddressBookDatabaseHelper dbHelper;
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    private static final int CONTACTS = 1;
    private static final int ONE_CONTACT = 2;

    static {
        uriMatcher.addURI(AUTHORITY, DatabaseDescription.Contact.TABLE_NAME, CONTACTS);
        uriMatcher.addURI(AUTHORITY, DatabaseDescription.Contact.TABLE_NAME + "/#", ONE_CONTACT);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new AddressBookDatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(DatabaseDescription.Contact.TABLE_NAME);

        switch (uriMatcher.match(uri)) {
            case ONE_CONTACT:
                builder.appendWhere(DatabaseDescription.Contact._ID + "=" + uri.getLastPathSegment());
                break;
            case CONTACTS:
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }

        Cursor cursor = builder.query(
                dbHelper.getReadableDatabase(), projection, selection, selectionArgs,
                null, null, sortOrder);

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        if (uriMatcher.match(uri) == CONTACTS) {
            long rowId = db.insert(DatabaseDescription.Contact.TABLE_NAME, null, values);
            if (rowId > 0) {
                Uri contactUri = ContentUris.withAppendedId(DatabaseDescription.Contact.CONTENT_URI, rowId);
                getContext().getContentResolver().notifyChange(contactUri, null);
                return contactUri;
            }
            throw new android.database.SQLException("Failed to insert row into " + uri);
        } else {
            throw new UnsupportedOperationException("Unknown URI: " + uri);
        }
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int count;
        switch (uriMatcher.match(uri)) {
            case ONE_CONTACT:
                String id = uri.getLastPathSegment();
                count = db.update(DatabaseDescription.Contact.TABLE_NAME, values, DatabaseDescription.Contact._ID + "=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }

        if (count > 0) getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int count;
        switch (uriMatcher.match(uri)) {
            case ONE_CONTACT:
                String id = uri.getLastPathSegment();
                count = db.delete(DatabaseDescription.Contact.TABLE_NAME, DatabaseDescription.Contact._ID + "=?", new String[]{id});
                break;
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }

        if (count > 0) getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }
}