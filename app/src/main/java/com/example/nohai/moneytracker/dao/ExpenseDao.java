package com.example.nohai.moneytracker.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;


import com.example.nohai.moneytracker.Database.Category;
import com.example.nohai.moneytracker.Database.Expense;

import java.util.List;

@Dao
public interface  ExpenseDao {


        @Query("SELECT * from expense_table ORDER BY date")
        List<Expense> getExpenses();

        @Query("SELECT * from expense_table ORDER BY date DESC")
        LiveData<List<Expense>> getChronologicalExpenses();

        @Insert
        void insert(Expense expense);

//        @Query("DELETE FROM expense_table")
//        void deleteAll();

        @Query("select sum(price) FROM expense_table where  Date(date)=Date(:theDate)")
        double getPriceSum(String theDate);

        @Query("select sum(price) FROM expense_table " +
                "where  Date(date) between Date(:startDate) and Date(:endDate)")
        double getPriceSumBetween(String startDate,String endDate);

        @Query("SELECT sum(price) FROM expense_table " +
                "where strftime('%m',Date(date)) = strftime('%m',Date(:theDate)) and "+
                "strftime('%Y',Date(date)) = strftime('%Y',Date(:theDate))")
        double getPriceSumForMonth(String theDate);

        @Query("SELECT sum(price) FROM expense_table " +
                "where strftime('%m',Date(date)) = strftime('%m',Date(:theDate)) and categoryId=:catId")
        double getPriceSumForMonthByCategory(int catId,String theDate);

        @Query("SELECT sum(price) FROM expense_table " +
                "where strftime('%Y',Date(date)) = strftime('%Y',Date(:theDate)) ;")
        double getPriceSumForYear(String theDate);

        @Query("SELECT sum(price) FROM expense_table " +
                "where strftime('%Y',Date(date)) = strftime('%Y',Date(:theDate)) and categoryId=:catId")
        double getPriceSumForYearByCategory(int catId,String theDate);


        @Query("SELECT sum(price) FROM expense_table " +
                "where strftime('%Y',Date(date)) = :theDate and categoryId=:catId")
        double getPriceSumForJustYearByCategory(int catId,String theDate);

        @Query("select sum(price) FROM expense_table where  Date(date)=Date(:theDate) and categoryId=:catId")
        double getPriceSumByCategory(int catId,String theDate);

        @Query("select sum(price) FROM expense_table where  " +
                "Date(date) between Date(:startDate) and Date(:endDate) and categoryId=:catId")
        double getPriceSumBetweenByCategory(int catId,String startDate, String endDate);

        @Query("SELECT sum(price) FROM expense_table " +
                "where categoryId=:catId")
        double getAllTimeSumByCategory(int catId);

//        @Query("update expense_category set categoryName=(:name) where categoryId=(:myId)")
//        void setCategoryName(String name, int myId)

    }
