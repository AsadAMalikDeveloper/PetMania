package com.example.petmania.database.local;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.petmania.database.modeldb.Favourite;


@Database(entities = {Favourite.class},version = 1)
public abstract class PetManiaRoomDatabase extends RoomDatabase {

    public abstract FavouriteDAO dao();
    private static PetManiaRoomDatabase instance;

    public static PetManiaRoomDatabase getDatabase(Context context){
        if (instance==null){
            synchronized (PetManiaRoomDatabase.class){
                if (instance==null){
                    instance= Room.databaseBuilder(context.getApplicationContext(),
                            PetManiaRoomDatabase.class,
                            "favourite_database")
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }

        return instance;
    }
}
