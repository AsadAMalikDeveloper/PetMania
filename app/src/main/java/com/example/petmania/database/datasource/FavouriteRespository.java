package com.example.petmania.database.datasource;

import com.example.petmania.database.modeldb.Favourite;

import java.util.List;

import io.reactivex.Flowable;

public class FavouriteRespository implements IFavouriteDataSource{
    private IFavouriteDataSource favouriteDataSource;

    public FavouriteRespository(IFavouriteDataSource favouriteDataSource) {
        this.favouriteDataSource = favouriteDataSource;
    }
    private static FavouriteRespository instance;
    public static FavouriteRespository getInstance(IFavouriteDataSource favouriteDataSource){
        if (instance==null){
            instance= new FavouriteRespository(favouriteDataSource);
        }
        return instance;
    }
    @Override
    public void delete(Favourite favourite) {
        favouriteDataSource.delete(favourite);
    }

    @Override
    public int isFav(int itemId) {
        return favouriteDataSource.isFav(itemId);
    }

    @Override
    public Flowable<List<Favourite>> getFavItems() {
        return favouriteDataSource.getFavItems();
    }

    @Override
    public void insertFav(Favourite... favourites) {
        favouriteDataSource.insertFav(favourites);
    }


}
