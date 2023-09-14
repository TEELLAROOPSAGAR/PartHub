package com.example.spareit.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

@Database(entities={Items.class},version = 1,exportSchema = false)
@TypeConverters(Convertors.class)
public abstract class AppDatabase extends RoomDatabase {

    public abstract UserDao userDao();


    private static AppDatabase INSTANCE;

    public static AppDatabase getDbInstance(Context context){

        if(INSTANCE==null){
            INSTANCE= Room.databaseBuilder(context.getApplicationContext(),AppDatabase.class,"ShopBase")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        }

        return INSTANCE;
    }

}
