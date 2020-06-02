package com.example.petmania.database.local;

import com.example.petmania.database.datasource.IFavouriteDataSource;
import com.example.petmania.database.modeldb.Favourite;

import java.util.List;

import io.reactivex.Flowable;

public class FavouriteDataSource implements IFavouriteDataSource {

    private FavouriteDAO favouriteDAO;
    private static FavouriteDataSource instance;

    public FavouriteDataSource(FavouriteDAO favouriteDAO) {
        this.favouriteDAO = favouriteDAO;
    }

    public static FavouriteDataSource getInstance(FavouriteDAO favouriteDAO){
        if (instance==null){
            instance = new FavouriteDataSource(favouriteDAO);
        }
        return instance;
    }

    @Override
    public void delete(Favourite favourite) {
        favouriteDAO.delete(favourite);
    }

    @Override
    public int isFav(int itemId) {
        return favouriteDAO.isFav(itemId);
    }

    @Override
    public Flowable<List<Favourite>> getFavItems() {
        return favouriteDAO.getFavItems();
    }

    @Override
    public void insertFav(Favourite... favourites) {
        favouriteDAO.insertFav(favourites);
    }
}
