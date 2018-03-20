package com.example.nohai.moneytracker;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface  ExpenseDao {

        @Query("SELECT * from expense_table ORDER BY id DESC")
        List<Expense> getExpenses();

        @Insert
        void insert(Expense expense);

        @Query("DELETE FROM expense_table")
        void deleteAll();
    }
