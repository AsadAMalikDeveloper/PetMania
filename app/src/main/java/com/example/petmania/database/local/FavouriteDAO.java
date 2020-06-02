package com.example.petmania.database.local;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.petmania.database.modeldb.Favourite;

import java.util.List;

import io.reactivex.Flowable;

@Dao
public interface FavouriteDAO {


    @Delete
    void delete(Favourite favourite);

    @Query("select EXISTS (SELECT 1 FROM Favourite WHERE ad_id=:itemId)")
    int isFav(int itemId);

    @Query("select * from Favourite")
    Flowable<List<Favourite>> getFavItems();

    @Insert
    void insertFav(Favourite...favourites);

}
