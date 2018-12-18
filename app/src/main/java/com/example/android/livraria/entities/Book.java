package com.example.android.livraria.entities;

public class Book {
    private String name;
    private int price;
    private int quantity;
    private String supName;
    private int phone;

    public Book(String name, int price, int quantity, String supName, int phone){
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.supName = supName;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getPhone() {
        return phone;
    }
}
