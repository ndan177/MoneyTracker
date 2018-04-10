package com.example.nohai.moneytracker.Database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.example.nohai.moneytracker.Utils.DbBitmapUtility;

@Entity(tableName = "category_icon_table")
public class CategoryIcon {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @NonNull
    @TypeConverters({DbBitmapUtility.class})
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB,name = "categoryIcon")
    private byte[] mCategoryIcon;


    public CategoryIcon( @NonNull byte[]  mCategoryIcon) {
        this.mCategoryIcon = mCategoryIcon;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public byte[] getCategoryIcon(){return this.mCategoryIcon;}

}