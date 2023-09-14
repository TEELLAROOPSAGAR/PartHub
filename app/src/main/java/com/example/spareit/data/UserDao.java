package com.example.spareit.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDao {
    @Query("SELECT * FROM Items")
    List<Items> getItems();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertItems(Items... items);

    @Query("DELETE FROM Items WHERE uid=:id")
    void deleteItem(int id);
}
