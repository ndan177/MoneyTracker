package com.example.nohai.moneytracker;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.database.Cursor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.example.nohai.moneytracker.Database.Category;
import com.example.nohai.moneytracker.Database.CategoryIcon;
import com.example.nohai.moneytracker.Database.Expense;
import com.example.nohai.moneytracker.Database.Income;
import com.example.nohai.moneytracker.dao.CategoryDao;
import com.example.nohai.moneytracker.dao.CategoryIconDao;
import com.example.nohai.moneytracker.dao.ExpenseDao;
import com.example.nohai.moneytracker.dao.IncomeDao;
import java.io.ByteArrayOutputStream;



@Database(entities = {Category.class,Expense.class,CategoryIcon.class,Income.class}, version = 7)

public abstract class AppDatabase extends RoomDatabase {


    public abstract CategoryDao categoryDao();
    public abstract ExpenseDao expenseDao();
    public abstract CategoryIconDao categoryIconDao();
    public abstract IncomeDao incomeDao();
    public static String DATABASE_NAME = "Database";
    private static AppDatabase INSTANCE;
    public  static Context myContext;


    static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, DATABASE_NAME )
                            // Wipes and rebuilds instead of migrating if no Migration object.

                            .fallbackToDestructiveMigration()
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        myContext=context;
        return INSTANCE;
    }

    /**
     * Override the onOpen method to populate the database.
     * For this sample, we clear the database every time it is created .
     */
    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback(){

        @Override
        public void onOpen (@NonNull SupportSQLiteDatabase db){
            super.onOpen(db);

               Cursor mCursor = db.query("SELECT count(*) FROM category_table", null);

               mCursor.moveToFirst();
               int counter = mCursor.getInt(0);
               if (counter<1)
                   new PopulateDbAsync(INSTANCE).execute();

        }
    };

    /**
     * Populate the database in the background.
     * If you want to start with more categories, just add them.
     */

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final CategoryDao mDao;
        private final CategoryIconDao mIconDao;
//        Bitmap bmp;
//        ByteArrayOutputStream stream;
//        byte[] byteArray;
        CategoryIcon categoryIcon;


        PopulateDbAsync(AppDatabase db) {
            mDao = db.categoryDao();
            mIconDao = db.categoryIconDao();
        }
        void insertIcon(int drawable)
        {
//            bmp = BitmapFactory.decodeResource(myContext.getResources(),drawable);
//            stream = new ByteArrayOutputStream();
//            bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
//            byteArray = stream.toByteArray();
//            bmp.recycle();
//            categoryIcon = new CategoryIcon(byteArray);

            categoryIcon = new CategoryIcon(drawable);
            mIconDao.insert(categoryIcon);
        }

        @Override
        protected Void doInBackground(final Void... params) {
            mDao.deleteAll();
            mIconDao.deleteAll();

            insertIcon(R.drawable.icons8_ingredients_48);
            insertIcon(R.drawable.icons8_car_48);
            insertIcon(R.drawable.icons8_hair_brush_48);
            insertIcon(R.drawable.icons8_stethoscope_48);
            insertIcon(R.drawable.icons8_clothes_48);
            insertIcon(R.drawable.icons8_gas_station_48);
            insertIcon(R.drawable.icons8_home_48);


            Category category = new Category("Food",1);
            mDao.insert(category);
            category = new Category("Car",2);
            mDao.insert(category);
            category = new Category("Beauty",3);
            mDao.insert(category);
            category = new Category("Health",4);
            mDao.insert(category);
            category = new Category("Clothes",5);
            mDao.insert(category);
            category = new Category("Transport",6);
            mDao.insert(category);
            category = new Category("Home",7);
            mDao.insert(category);
            return null;
        }
    }

}
