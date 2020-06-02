package com.example.petmania.database.datasource;


import com.example.petmania.database.modeldb.Favourite;

import java.util.List;

import io.reactivex.Flowable;

public interface IFavouriteDataSource {


    void delete(Favourite favourite);
    int isFav(int itemId);
    Flowable<List<Favourite>> getFavItems();
    void insertFav(Favourite...favourites);
}
