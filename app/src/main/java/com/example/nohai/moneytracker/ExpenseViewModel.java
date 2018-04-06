package com.example.nohai.moneytracker;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.example.nohai.moneytracker.Database.Expense;

import java.util.List;

/**
 * Created by nohai on 4/4/2018.
 */

public class ExpenseViewModel extends AndroidViewModel {

    public ExpenseRepository mExpense;
    // Using LiveData and caching what getAlphabetizedWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    private LiveData<List<Expense>> mAllExpenses;

    public ExpenseViewModel (Application application) {
        super(application);
        mExpense = new ExpenseRepository(application);
        mAllExpenses = mExpense.getAllExpenses();
    }

    LiveData<List<Expense>> getAllExpenses() { return mAllExpenses; }

    public void insert(Expense expense) { mExpense.insert(expense); }

}