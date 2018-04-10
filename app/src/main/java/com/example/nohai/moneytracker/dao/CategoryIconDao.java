package com.example.nohai.moneytracker.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.nohai.moneytracker.Database.CategoryIcon;


/**
 * Created by nohai on 3/25/2018.
 */
@Dao
public interface CategoryIconDao {

    @Insert
    void insert(CategoryIcon categoryIcon);

    @Query("DELETE FROM category_icon_table")
    void deleteAll();

    @Query("select categoryIcon  FROM category_icon_table where id=:categoryId")
    byte[] getFirst(int categoryId);
}
