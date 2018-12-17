package com.example.android.livraria;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.livraria.data.BooksContract;
import com.example.android.livraria.entities.Book;

import java.util.List;

public class BookCursorAdapter extends RecyclerView.Adapter<BookCursorAdapter.ViewHolder> {

    private Cursor books;
    private LayoutInflater mInflater;
    private BookClickListener bookClickListener;

    public BookCursorAdapter(Context context, Cursor books, BookClickListener bookClickListener) {
        this.mInflater = LayoutInflater.from(context);
        this.books = books;
        this.bookClickListener = bookClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = mInflater.inflate(R.layout.item_book, viewGroup, false);
        return new BookCursorAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        //final Book currentBook = books.get;
        try {
            int nameColumn = books.getColumnIndexOrThrow(BooksContract.BookEntry.COLUMN_BOOK_NAME);
            int priceColumn = books.getColumnIndexOrThrow(BooksContract.BookEntry.COLUMN_BOOK_PRICE);
            int quantityColumn = books.getColumnIndexOrThrow(BooksContract.BookEntry.COLUMN_BOOK_QUANTITY);

            books.moveToPosition(i);

            String name = books.getString(nameColumn);
            String price = books.getString(priceColumn);
            String quantity = books.getString(quantityColumn);


            viewHolder.myBookName.setText(name);
            viewHolder.myBookPrice.setText(price);
            viewHolder.myBookQuantity.setText(quantity);
        }catch (Exception ex){
            Log.e("BOOKCURSORADAPTER", ex.toString());
        }
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bookClickListener != null) {
                    bookClickListener.onBookSelected(viewHolder.getAdapterPosition());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (books == null) {
            return 0;
        }
        return books.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView myBookName;
        TextView myBookPrice;
        TextView myBookQuantity;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            myBookName = itemView.findViewById(R.id.productName);
            myBookPrice = itemView.findViewById(R.id.productPrice);
            myBookQuantity = itemView.findViewById(R.id.productQuantity);
        }
    }

    /**
     * Change the underlying cursor to a new cursor. If there is an existing cursor it will be
     * closed.
     */
    public void changeCursor(Cursor cursor) {
        Cursor old = swapCursor(cursor);
        if (old != null) {
            old.close();
        }
    }

    /**
     * Swap in a new Cursor, returning the old Cursor.  Unlike
     * {@link #changeCursor(Cursor)}, the returned old Cursor is <em>not</em>
     * closed.
     */
    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor == books) {
            return null;
        }
        final Cursor oldCursor = books;
        books = newCursor;
        if (books != null) {
            //TODO OBSERVADOR
        }
        return oldCursor;
    }
}
