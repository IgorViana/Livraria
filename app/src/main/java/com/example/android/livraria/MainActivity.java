package com.example.android.livraria;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.livraria.data.BookDbHelper;
import com.example.android.livraria.data.BooksContract;
import com.example.android.livraria.data.BooksContract.BookEntry;
import com.example.android.livraria.entities.Book;

import java.util.List;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, BookClickListener {
    private static final int BOOK_LOADER = 0;
    private BookCursorAdapter mAdapter;
    private RecyclerView list;
    private View emptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddLivro.class);
                startActivity(intent);
            }
        });
        list = findViewById(R.id.listaBooks);
        emptyView = findViewById(R.id.empty_view);

        mAdapter = new BookCursorAdapter(this, null, this);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(mAdapter);

        getLoaderManager().initLoader(BOOK_LOADER, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_BOOK_NAME,
                BookEntry.COLUMN_BOOK_QUANTITY,
                BookEntry.COLUMN_BOOK_PRICE,
        };

        return new CursorLoader(this,
                BookEntry.CONTENT_URI,
                projection,
                null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {
            list.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        } else {
            list.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        }
        mAdapter.changeCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }

    @Override
    public void onBookSelected(long bookId) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        Uri currentBookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, bookId);
        Log.i("URI", currentBookUri.toString());
        intent.setData(currentBookUri);
        startActivity(intent);
    }

    @Override
    public void onSaleSelected(long index, int quantity) {
        if (quantity > 0) {
            quantity --;

            ContentValues values = new ContentValues();
            values.put(BookEntry.COLUMN_BOOK_QUANTITY, quantity);

            Uri currentBookUri = ContentUris.withAppendedId(BookEntry.CONTENT_URI, index);
            int rowsUpdated = getContentResolver().update(currentBookUri, values, null, null);
            if (rowsUpdated == 0) {
                Toast.makeText(this, getString(R.string.errorUpdating), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.updated), Toast.LENGTH_SHORT).show();
            }
            return;
        }
        Toast.makeText(this, getString(R.string.errorQuantity), Toast.LENGTH_SHORT).show();
    }
}
