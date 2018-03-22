package com.example.nohai.moneytracker;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import java.sql.Date;


/**
 * Created by nohai on 3/19/2018.
 */

@Entity(tableName = "expense_table",
        foreignKeys = @ForeignKey(entity = Category.class,
                parentColumns = "id",
                childColumns = "categoryId"
                                )
        )
public class Expense {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    private int categoryId;

    @NonNull
    public double price;

    @NonNull

    public Date date;

    public String notes;


    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public int getId() {
        return id;
    }



    public void setId(int id) {
        this.id = id;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Expense(double price)
    {
        this.price = price;
    }

    public Expense()
    {
        this(0.0);
    }

}