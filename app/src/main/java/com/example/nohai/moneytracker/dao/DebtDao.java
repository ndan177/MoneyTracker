package com.example.nohai.moneytracker.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import com.example.nohai.moneytracker.Database.Debt;

import java.util.List;

@Dao
public interface DebtDao {

    @Insert
    void insert(Debt debt);

    @Query("SELECT * from Debt_table ORDER BY date DESC")
    LiveData<List<Debt>> getChronologicalDebts();

    @Query("SELECT * from Debt_table where borrowTo = 1 and resolved=0 ORDER BY date DESC")
    LiveData<List<Debt>> getDebtsTo();

    @Query("SELECT * from Debt_table where borrowTo = 0 and resolved=0 ORDER BY date DESC")
    LiveData<List<Debt>> getDebtsFrom();

    @Query("SELECT * from Debt_table where resolved=1 ORDER BY date DESC")
    LiveData<List<Debt>> getResolvedDebts();

}
