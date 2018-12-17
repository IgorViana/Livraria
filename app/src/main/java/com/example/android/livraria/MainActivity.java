package com.example.android.livraria;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.android.livraria.data.BookDbHelper;
import com.example.android.livraria.data.BooksContract;
import com.example.android.livraria.data.BooksContract.BookEntry;
import com.example.android.livraria.entities.Book;

import java.util.List;


public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, BookClickListener {
    private static final int PET_LOADER = 0;
    private BookCursorAdapter mAdapter;

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
        RecyclerView list = findViewById(R.id.listaBooks);
        mAdapter = new BookCursorAdapter(this, null, this);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(mAdapter);
        getLoaderManager().initLoader(PET_LOADER, null, this);
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
        mAdapter.changeCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
        //TODO GET THE ADAPTER AND SWAP THE CURRENT CURSOR FOR A NEW ONE NULL
    }

    @Override
    public void onBookSelected(int book) {

    }


}
