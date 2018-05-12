package com.example.nohai.moneytracker.Database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.example.nohai.moneytracker.Utils.DateConverter;

import java.util.Date;


@Entity(tableName = "debt_table")
public class Debt {
    @PrimaryKey(autoGenerate = true)
    private int id;


    @NonNull
    @TypeConverters({DateConverter.class})
    public Date date;

    public int contactId;

    public String notes;

    @NonNull
    public double price;

    @TypeConverters({DateConverter.class})
    public Date dateLimit;

    public boolean resolved;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Debt(boolean resolved)
    {
        this.resolved = resolved;
    }

    public Debt()
    {
        this(false);
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setDateLimit(Date dateLimit) {
        this.dateLimit = dateLimit;
    }

}
