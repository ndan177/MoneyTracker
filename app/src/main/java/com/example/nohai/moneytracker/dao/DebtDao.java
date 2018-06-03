package com.example.nohai.moneytracker.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.nohai.moneytracker.Database.Debt;

import java.util.List;

@Dao
public interface DebtDao {

    @Insert
    void insert(Debt debt);

    @Update
    void update(Debt debt);

    @Delete
    void delete(Debt debt);

    @Query("SELECT * from Debt_table ORDER BY date DESC")
    LiveData<List<Debt>> getChronologicalDebts();

    @Query("SELECT * from Debt_table where borrowTo = 1 and resolved=0 ORDER BY date DESC")
    LiveData<List<Debt>> getDebtsTo();

    @Query("SELECT * from Debt_table where borrowTo = 0 and resolved=0 ORDER BY date DESC")
    LiveData<List<Debt>> getDebtsFrom();

    @Query("SELECT * from Debt_table where resolved=1 ORDER BY date DESC")
    LiveData<List<Debt>> getResolvedDebts();

    @Query("SELECT * from Debt_table where borrowTo = 1 and resolved=0 ORDER BY date DESC")
    List<Debt> getDebtsTo2();

    @Query("SELECT * from Debt_table where borrowTo = 0 and resolved=0 ORDER BY date DESC")
    List<Debt> getDebtsFrom2();

    @Query("SELECT * from Debt_table where resolved=1 ORDER BY date DESC")
     List<Debt> getResolvedDebts2();

}
