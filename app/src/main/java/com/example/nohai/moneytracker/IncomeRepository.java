package com.example.nohai.moneytracker;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.example.nohai.moneytracker.Database.Income;
import com.example.nohai.moneytracker.dao.IncomeDao;

import java.util.List;

/**
 * Created by nohai on 3/24/2018.
 */

public class IncomeRepository {


    private IncomeDao mIncomeDao;
    private LiveData<List<Income>> mAllIncomes;


    IncomeRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mIncomeDao = db.incomeDao();
        mAllIncomes = mIncomeDao.getChronologicalIncomes();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.

    LiveData<List<Income>> getAllIncomes() {
        return mAllIncomes;
    }

    double getSum(String myDate){return mIncomeDao.getPriceSum(myDate); }

    // You must call this on a non-UI thread or your app will crash.
    // Like this, Room ensures that you're not doing any long running operations on the main
    // thread, blocking the UI.
    public void insert (Income Income) {
        new insertAsyncTask(mIncomeDao).execute(Income);
    }

    private static class insertAsyncTask extends AsyncTask<Income, Void, Void> {

        private IncomeDao mAsyncTaskDao;

        insertAsyncTask(IncomeDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Income... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }
}
