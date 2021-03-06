package com.example.nohai.moneytracker;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.example.nohai.moneytracker.Database.Debt;
import com.example.nohai.moneytracker.dao.DebtDao;

import java.util.List;


public class DebtRepository {


    private DebtDao mDebtDao;
    private LiveData<List<Debt>> mAllDebts;
    private LiveData<List<Debt>> mAllDebtsToReceive;
    private LiveData<List<Debt>> mAllDebtsToPay;
    private LiveData<List<Debt>> mResolvedDebts;


    DebtRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mDebtDao = db.debtDao();
        mAllDebts = mDebtDao.getChronologicalDebts();
        mAllDebtsToReceive = mDebtDao.getDebtsToReceiveLive();
        mAllDebtsToPay = mDebtDao.getDebtsToPayLive();
        mResolvedDebts = mDebtDao.getResolvedDebts();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.

    LiveData<List<Debt>> getAllDebts() {
        return mAllDebts;
    }

    LiveData<List<Debt>> getAllDebtsToReceive() {
        return mAllDebtsToReceive;
    }

    LiveData<List<Debt>> getAllDebtsToPay() {
        return mAllDebtsToPay;
    }

    LiveData<List<Debt>> getResolvedDebts() {
        return mResolvedDebts;
    }

   // double getSum(String myDate){return mDebtDao.getPriceSum(myDate); }

    // You must call this on a non-UI thread or your app will crash.
    // Like this, Room ensures that you're not doing any long running operations on the main
    // thread, blocking the UI.
    public void insert (Debt debt) {
        new insertAsyncTask(mDebtDao).execute(debt);
    }

    private static class insertAsyncTask extends AsyncTask<Debt, Void, Void> {

        private DebtDao mAsyncTaskDao;

        insertAsyncTask(DebtDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Debt... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }
}
