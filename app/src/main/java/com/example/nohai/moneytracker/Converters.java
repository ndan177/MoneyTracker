package com.example.nohai.moneytracker;

import android.arch.persistence.room.TypeConverter;

import java.sql.Date;

/**
 * Created by nohai on 3/22/2018.
 */

public class Converters {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
