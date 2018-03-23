package com.example.nohai.moneytracker.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;


import com.example.nohai.moneytracker.Database.Expense;

import java.util.List;

@Dao
public interface  ExpenseDao {


        @Query("SELECT * from expense_table ORDER BY id DESC")
        List<Expense> getExpenses();

        @Insert
        void insert(Expense expense);

        @Query("DELETE FROM expense_table")
        void deleteAll();

        @Query("select sum(price) FROM expense_table where  Date(date)=Date(:theDate)")
        double getPriceSum(String theDate);
    }
