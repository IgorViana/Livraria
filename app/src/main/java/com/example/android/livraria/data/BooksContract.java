package com.example.android.livraria.data;

import android.provider.BaseColumns;

public class BooksContract {

    private BooksContract(){}

    public static  final class BookEntry implements BaseColumns{
        public final static String TABLE_NAME = "books";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_BOOK_NAME = "name";
        public final static String COLUMN_BOOK_PRICE = "price";
        public final static String COLUMN_BOOK_QUANTITY = "quantity";
        public final static String COLUMN_SUPPLIER_NAME = "sup_name";
        public final static String COLUMN_SUPPLIER_PHONE = "sup_phone";
    }

}
