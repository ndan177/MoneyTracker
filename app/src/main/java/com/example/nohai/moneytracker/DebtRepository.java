package com.example.nohai.moneytracker;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.example.nohai.moneytracker.Database.Debt;
import com.example.nohai.moneytracker.Database.Debt;
import com.example.nohai.moneytracker.dao.DebtDao;

import java.util.List;


public class DebtRepository {


    private DebtDao mDebtDao;
    private LiveData<List<Debt>> mAllDebts;
    private LiveData<List<Debt>> mAllDebtsTo;
    private LiveData<List<Debt>> mAllDebtsFrom;
    private LiveData<List<Debt>> mResolvedDebts;

    // Note that in order to unit test the WordRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples

    DebtRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mDebtDao = db.debtDao();
        mAllDebts = mDebtDao.getChronologicalDebts();
        mAllDebtsTo = mDebtDao.getDebtsTo();
        mAllDebtsFrom= mDebtDao.getDebtsFrom();
        mResolvedDebts = mDebtDao.getResolvedDebts();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.

    LiveData<List<Debt>> getAllDebts() {
        return mAllDebts;
    }

    LiveData<List<Debt>> getAllDebtsTo() {
        return mAllDebtsTo;
    }

    LiveData<List<Debt>> getAllDebtsFrom() {
        return mAllDebtsFrom;
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
