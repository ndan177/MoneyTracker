package com.example.nohai.moneytracker;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.example.nohai.moneytracker.Database.Debt;

import java.util.List;

public class DebtViewModel extends AndroidViewModel {

        public DebtRepository mDebt;
        // Using LiveData and caching what getAlphabetizedWords returns has several benefits:
        // - We can put an observer on the data (instead of polling for changes) and only update the
        //   the UI when the data actually changes.
        // - Repository is completely separated from the UI through the ViewModel.
        private LiveData<List<Debt>> mAllDebts;

        public DebtViewModel (Application application) {
            super(application);
            mDebt = new DebtRepository(application);
            mAllDebts = mDebt.getAllDebts();
        }

        LiveData<List<Debt>> getAllDebts() { return mAllDebts; }

        public void insert(Debt Debt) { mDebt.insert(Debt); }

    }
