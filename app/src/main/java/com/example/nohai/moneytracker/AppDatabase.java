package com.example.nohai.moneytracker;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import android.database.Cursor;

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


@Database(entities = {Category.class,Expense.class,CategoryIcon.class,Income.class}, version = 5)

public abstract class AppDatabase extends RoomDatabase {

    public abstract CategoryDao categoryDao();
    public abstract ExpenseDao expenseDao();
    public abstract CategoryIconDao categoryIconDao();
    public abstract IncomeDao incomeDao();
    public static String DATABASE_NAME = "Database";

    private static AppDatabase INSTANCE;

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

               Cursor mCursor = db.query("SELECT count(*) FROM category_table", null);
              // db.query("update expense_table set categoryName='asd' ", null);
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

        PopulateDbAsync(AppDatabase db) {
            mDao = db.categoryDao();
            mIconDao = db.categoryIconDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            // Start the app with a clean database every time.
            // Not needed if you only populate on creation.
           mDao.deleteAll();
           mIconDao.deleteAll();
            //
//            int i;
//            for(i=0;i<1;i++)
//                int home = R.drawable.databaseIcons;
//            context.getResources().getXml(R.xml.samplexml);
//            Resources res = getActivity().getApplicationContext()
//            Bitmap image = BitmapFactory.decodeResource(R.id.s
//                    // convert bitmap to byte
//
//            ByteArrayOutputStream stream = new ByteArrayOutputStream();
//
//            image.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//
//            byte imageInByte[] = stream.toByteArray();
            //
//           CategoryIcon categoryIcon=new CategoryIcon();
//           mIconDao.insert(categoryIcon);

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
