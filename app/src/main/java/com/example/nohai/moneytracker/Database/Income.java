package com.example.nohai.moneytracker.Database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.example.nohai.moneytracker.Utils.DateConverter;

import java.util.Date;

/**
 * Created by nohai on 3/28/2018.
 */
@Entity(tableName = "income_table"

)
public class Income {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    public double price;

    @NonNull
    @TypeConverters({DateConverter.class})
    public Date date;

    public String notes;



    public int getId() {
        return id;
    }



    public void setId(int id) {
        this.id = id;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Income(double price)
    {
        this.price = price;
    }

    public Income()
    {
        this(0.0);
    }

}