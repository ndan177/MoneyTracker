package com.example.nohai.moneytracker.Utils;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateHelper {

   public static Date firstWeekDay(String stringDate)//return first day from week, date format
    {
        Date date;
        Date weekStart=null;
        try {
            date =new SimpleDateFormat("dd-MMM-yyyy").parse(stringDate);

            Calendar c = Calendar.getInstance();

            c.setTime(date);

            int dayOfWeek = c.get(Calendar.DAY_OF_WEEK) - c.getFirstDayOfWeek();//sunday

            c.add(Calendar.DAY_OF_MONTH, -dayOfWeek);

            weekStart = c.getTime();

        }catch(Exception Ex){}

        return weekStart;
    }
    public static Date lastWeekDay(String stringDate)//return last day from week, date format
    {
        Date date;
        Date weekEnd=null;
        try {
            date =new SimpleDateFormat("dd-MMM-yyyy").parse(stringDate);

            Calendar c = Calendar.getInstance();
            c.setFirstDayOfWeek(Calendar.MONDAY);
            c.setTime(date);
            int dayOfWeek = c.get(Calendar.DAY_OF_WEEK) - c.getFirstDayOfWeek();//sunday
            if (c.get(Calendar.DAY_OF_WEEK)==1)dayOfWeek-=1;
            c.add(Calendar.DAY_OF_MONTH, -dayOfWeek);
            c.add(Calendar.DAY_OF_MONTH, 6);
            weekEnd = c.getTime();
        }catch(Exception Ex){}

        return weekEnd;
    }
    public static String getMonthName(int month) {
        return new DateFormatSymbols().getShortMonths()[month-1];
    }
   public static String displayDateFormatList(Date date)
    {
        return new SimpleDateFormat("dd MMMM yyyy").format(date);
    }
}
