package com.example.nohai.moneytracker;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.example.nohai.moneytracker.Database.Expense;
import com.example.nohai.moneytracker.dao.ExpenseDao;

import java.util.List;

/**
 * Created by nohai on 3/24/2018.
 */

public class ExpenseRepository {


    private ExpenseDao mExpenseDao;
    private LiveData<List<Expense>> mAllExpenses;


    ExpenseRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mExpenseDao = db.expenseDao();
        mAllExpenses = mExpenseDao.getChronologicalExpenses();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.

    LiveData<List<Expense>> getAllExpenses() {
        return mAllExpenses;
    }

    double getSum(String myDate){return mExpenseDao.getPriceSum(myDate); }

    // You must call this on a non-UI thread or your app will crash.
    // Like this, Room ensures that you're not doing any long running operations on the main
    // thread, blocking the UI.
    public void insert (Expense expense) {
        new insertAsyncTask(mExpenseDao).execute(expense);
    }

    private static class insertAsyncTask extends AsyncTask<Expense, Void, Void> {

        private ExpenseDao mAsyncTaskDao;

        insertAsyncTask(ExpenseDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Expense... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }
}
