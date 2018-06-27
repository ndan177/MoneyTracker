package com.example.nohai.moneytracker.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.nohai.moneytracker.Database.Income;


import java.util.List;

/**
 * Created by nohai on 3/28/2018.
 */
@Dao
public interface IncomeDao {

    @Update
    void update(Income income);

    @Delete
    void delete(Income income);


    @Query("SELECT * from income_table ORDER BY date DESC")
    LiveData<List<Income>> getChronologicalIncomes();

    @Query("SELECT * from income_table where  Date(date)=Date(:theDate) ORDER BY date DESC")
    List<Income> getDayIncomes(String theDate);

    @Query("SELECT *  FROM income_table " +
            "where  Date(date) between Date(:startDate) and Date(:endDate)  ORDER BY date DESC")
    List<Income> getWeekIncomes(String startDate,String endDate);

    @Query("SELECT * from income_table " +
            "where strftime('%m',Date(date)) = strftime('%m',Date(:theDate)) and "+
            "strftime('%Y',Date(date)) = strftime('%Y',Date(:theDate)) ORDER BY date DESC")
    List<Income> getMonthIncomes(String theDate);

    @Query("SELECT * from income_table " +
            "where strftime('%Y',Date(date)) = strftime('%Y',Date(:theDate)) ORDER BY date DESC")
    List<Income> getYearIncomes(String theDate);


    @Query("SELECT * from income_table ORDER BY date")
    List<Income> getIncomes();

    @Query("select sum(price) FROM income_table where  Date(date)=Date(:theDate)")
    double getPriceSum(String theDate);

    @Query("select sum(price) FROM income_table " +
            "where  Date(date) between Date(:startDate) and Date(:endDate)")
    double getPriceSumBetween(String startDate,String endDate);

    @Query("SELECT sum(price) FROM income_table " +
            "where strftime('%m',Date(date)) = strftime('%m',Date(:theDate)) ;")
    double getPriceSumForMonth(String theDate);

    @Query("SELECT sum(price) FROM income_table " +
            "where strftime('%Y',Date(date)) = strftime('%Y',Date(:theDate)) ;")
    double getPriceSumForYear(String theDate);

    @Insert
    void insert(Income income);
}

