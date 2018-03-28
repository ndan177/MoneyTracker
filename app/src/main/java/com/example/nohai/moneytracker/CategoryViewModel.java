package com.example.nohai.moneytracker;

/**
 * Created by nohai on 3/16/2018.
 */

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import com.example.nohai.moneytracker.Database.Category;

import java.util.List;

/**
 * View Model to keep a reference to the word repository and
 * an up-to-date list of all categories.
 */

public class CategoryViewModel extends AndroidViewModel {

    public CategoryRepository mCategory;
    // Using LiveData and caching what getAlphabetizedWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    private LiveData<List<Category>> mAllCategories;

    public CategoryViewModel (Application application) {
        super(application);
        mCategory = new CategoryRepository(application);
        mAllCategories = mCategory.getAllCategories();
    }

    LiveData<List<Category>> getAllCategories() { return mAllCategories; }

    public void insert(Category category) { mCategory.insert(category); }

}