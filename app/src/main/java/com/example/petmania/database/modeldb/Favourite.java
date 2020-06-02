package com.example.petmania.database.modeldb;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity(tableName = "Favourite")
public class Favourite {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "ad_id")
    public String ad_id;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "price")
    public String price;

    @ColumnInfo(name = "image")
    public String image;

    @ColumnInfo(name = "location")
    public String location;
}
