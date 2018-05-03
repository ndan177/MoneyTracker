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

}
