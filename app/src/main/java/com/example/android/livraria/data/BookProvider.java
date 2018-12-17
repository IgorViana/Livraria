package com.example.android.livraria.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import static com.example.android.livraria.data.BooksContract.CONTENT_AUTHORITY;
import static com.example.android.livraria.data.BooksContract.PATH_BOOKS;

import com.example.android.livraria.data.BooksContract.BookEntry;


public class BookProvider extends ContentProvider {
    private static final int BOOKS = 10; //Access all the books
    private static final int BOOK_ID = 20; //Access one book by ID
    private BookDbHelper dbHelper;

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_BOOKS, BOOKS);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH_BOOKS + "#", BOOK_ID);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new BookDbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor;
        int match = uriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                cursor = database.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case BOOK_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{
                        String.valueOf(ContentUris.parseId(uri))};
                cursor = database.query(BookEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unkown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return BookEntry.CONTENT_LIST_TYPE;
            case BOOK_ID:
                return BookEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                long id = database.insert(BookEntry.TABLE_NAME, null, values);
                if (id == -1) {
                    return null;
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return ContentUris.withAppendedId(uri, id);
            default:
                throw new IllegalArgumentException("Cannot insert a book with this uri: " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
            case BOOK_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{
                        String.valueOf(ContentUris.parseId(uri))
                };
                int rowsDeleted = database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                if (rowsDeleted != -1) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsDeleted;
            default:
                throw new IllegalArgumentException("Cannot delete a book with this uri: " + uri);
        }
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        int match = uriMatcher.match(uri);
        switch (match) {
            case BOOK_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{
                        String.valueOf(ContentUris.parseId(uri))
                };
                int rowsUpdate = database.update(BookEntry.TABLE_NAME, values, selection, selectionArgs);
                if (rowsUpdate != -1) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                return rowsUpdate;
            default:
                throw new IllegalArgumentException("Cannot update a book with this uri: " + uri);
        }
    }
}
