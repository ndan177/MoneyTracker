package com.example.nohai.moneytracker;

import android.arch.persistence.db.SimpleSQLiteQuery;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import static java.sql.Types.NULL;


@Database(entities = {Category.class,Expense.class}, version = 6)
public abstract class AppDatabase extends RoomDatabase {

    public abstract CategoryDao categoryDao();

    public abstract ExpenseDao expenseDao();

    private static AppDatabase INSTANCE;

    static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "Database")
                            // Wipes and rebuilds instead of migrating if no Migration object.
                            // Migration is not part of this codelab.
                            .fallbackToDestructiveMigration()
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Override the onOpen method to populate the database.
     * For this sample, we clear the database every time it is created or opened.
     */
    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback(){

        @Override
        public void onOpen (@NonNull SupportSQLiteDatabase db){
            super.onOpen(db);

               Cursor mcursor = db.query("SELECT count(*) FROM category_table", null);
               mcursor.moveToFirst();
               int icount = mcursor.getInt(0);
               if (icount<1)
                   new PopulateDbAsync(INSTANCE).execute();

        }
    };

    /**
     * Populate the database in the background.
     * If you want to start with more categories, just add them.
     */
    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final CategoryDao mDao;

        PopulateDbAsync(AppDatabase db) {
            mDao = db.categoryDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            // Start the app with a clean database every time.
            // Not needed if you only populate on creation.
           mDao.deleteAll();

            Category category = new Category("Food");
            mDao.insert(category);
            category = new Category("Car");
            mDao.insert(category);
            category = new Category("Beauty");
            mDao.insert(category);
            category = new Category("Health");
            mDao.insert(category);
            category = new Category("Clothes");
            mDao.insert(category);
            category = new Category("Transport");
            mDao.insert(category);
            category = new Category("Home");
            mDao.insert(category);

            return null;
        }
    }

}
