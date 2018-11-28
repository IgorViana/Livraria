package com.example.android.livraria;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.example.android.livraria.data.BookDbHelper;
import com.example.android.livraria.data.BooksContract;
import com.example.android.livraria.data.BooksContract.BookEntry;

public class AddLivro extends AppCompatActivity {

    private EditText bookName;
    private EditText bookPrice;
    private EditText bookQuantity;
    private EditText bookSupName;
    private EditText bookSupPhone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_livro);

        bookName = findViewById(R.id.book_name);
        bookPrice = findViewById(R.id.book_price);
        bookQuantity = findViewById(R.id.book_quantity);
        bookSupName = findViewById(R.id.book_supplier_name);
        bookSupPhone = findViewById(R.id.book_supplier_phone);
    }

    public void insertBook(){
        String name = bookName.getText().toString().trim();
        int price = Integer.parseInt(bookPrice.getText().toString().trim());
        int quantity = Integer.parseInt(bookQuantity.getText().toString().trim());
        String supName = bookSupName.getText().toString().trim();
        int phone = Integer.parseInt(bookSupPhone.getText().toString().trim());

        ContentValues value = new ContentValues();
        value.put(BookEntry.COLUMN_BOOK_NAME, name);
        value.put(BookEntry.COLUMN_BOOK_PRICE, price);
        value.put(BookEntry.COLUMN_BOOK_QUANTITY, quantity);
        value.put(BookEntry.COLUMN_SUPPLIER_NAME, supName);
        value.put(BookEntry.COLUMN_SUPPLIER_PHONE, phone);

        BookCrud.insertData(this, value);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_livro, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                insertBook();
                finish();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
