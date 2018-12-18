package com.example.android.livraria;

import com.example.android.livraria.entities.Book;

public interface BookClickListener {
    void onBookSelected (long index);
    void onSaleSelected (long index, int quantity);
}
