package com.example.nohai.moneytracker;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.example.nohai.moneytracker.Database.Income;

import java.util.List;

/**
 * Created by nohai on 4/4/2018.
 */

public class IncomeViewModel extends AndroidViewModel {

    public IncomeRepository mIncome;
    // Using LiveData and caching what getAlphabetizedIncome returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    private LiveData<List<Income>> mAllIncomes;

    public IncomeViewModel (Application application) {
        super(application);
        mIncome = new IncomeRepository(application);
        mAllIncomes = mIncome.getAllIncomes();
    }

    LiveData<List<Income>> getAllIncomes() { return mAllIncomes; }

    public void insert(Income income) { mIncome.insert(income); }

}