package com.example.nohai.moneytracker.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.example.nohai.moneytracker.Database.Income;

import java.util.List;

/**
 * Created by nohai on 3/28/2018.
 */
@Dao
public interface IncomeDao {

    @Query("SELECT * from income_table ORDER BY id DESC")
    List<Income> getIncomings();

    @Query("select sum(price) FROM income_table where  Date(date)=Date(:theDate)")
    double getPriceSum(String theDate);

    @Query("select sum(price) FROM income_table " +
            "where  Date(date) between Date(:startDate) and Date(:endDate)")
    double getPriceSumBetween(String startDate,String endDate);

    @Query("SELECT sum(price) FROM income_table " +
            "where strftime('%m',Date(date)) = strftime('%m',Date(:theDate)) ;")
    double getPriceSumForMonth(String theDate);

    @Insert
    void insert(Income income);
}

