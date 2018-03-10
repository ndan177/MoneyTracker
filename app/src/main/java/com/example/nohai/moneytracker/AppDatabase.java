package com.example.nohai.moneytracker;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;


@Database(entities = Category.class, version = 3)
public abstract class AppDatabase extends RoomDatabase {
    public abstract CategoryDao categoryDao();
}
