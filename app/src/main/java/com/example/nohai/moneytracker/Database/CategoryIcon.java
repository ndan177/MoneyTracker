package com.example.nohai.moneytracker.Database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by nohai on 3/25/2018.
 */

@Entity(tableName = "category_icon_table")
public class CategoryIcon {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    @ColumnInfo(name = "categoryIcon")
    private byte mCategoryIcon;


    public CategoryIcon( @NonNull byte  mCategoryIcon) {
        this.mCategoryIcon = mCategoryIcon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte getCategoryIcon(){return this.mCategoryIcon;}

}