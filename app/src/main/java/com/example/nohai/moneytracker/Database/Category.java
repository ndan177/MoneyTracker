package com.example.nohai.moneytracker.Database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;


@Entity(tableName = "category_table",
            foreignKeys = @ForeignKey(entity = CategoryIcon.class,
                parentColumns = "id",
                childColumns = "categoryIconId"))
public class Category {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    @ColumnInfo(name = "category")
    private String mCategory;

    @NonNull
    @ColumnInfo(name = "categoryIconId")
    private int mCategoryIcon;

    @Ignore
    public double expensesCost;

    public Category( @NonNull String mCategory, @NonNull int mCategoryIcon) {
        this.mCategory = mCategory;
        this.mCategoryIcon = mCategoryIcon;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategory(){return this.mCategory;}

    public int getCategoryIcon(){return this.mCategoryIcon;}

}