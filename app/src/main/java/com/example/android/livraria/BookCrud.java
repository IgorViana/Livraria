package com.example.android.livraria;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.android.livraria.data.BookDbHelper;
import com.example.android.livraria.data.BooksContract;
import com.example.android.livraria.data.BooksContract.BookEntry;

public class BookCrud {

    public static void insertData(Context context, ContentValues values) {
        BookDbHelper mDbHelper = new BookDbHelper(context);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        db.insert(BookEntry.TABLE_NAME, null, values);
    }

    public static Cursor queryData(Context context) {
        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        BookDbHelper mDbHelper = new BookDbHelper(context);
        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_NAME,
                BookEntry.COLUMN_BOOK_PRICE,
                BookEntry.COLUMN_BOOK_QUANTITY,
                BookEntry.COLUMN_SUPPLIER_NAME,
                BookEntry.COLUMN_SUPPLIER_PHONE,
        };
         return db.query(BookEntry.TABLE_NAME, projection, null, null, null, null, null);
    }
}
