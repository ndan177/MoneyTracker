package com.example.nohai.moneytracker;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.example.nohai.moneytracker.Database.Expense;
import com.example.nohai.moneytracker.Utils.RecyclerItemClickListener;
import java.util.List;

public class ExpenseList extends AppCompatActivity {

    private ExpenseViewModel mExpenseViewModel;
    private LinearLayout myList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_list);

      //  myList = findViewById(R.id.myList);
       // myList.setClipToOutline(true);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("Expenses");
        actionbar.setDisplayHomeAsUpEnabled(true);



        final RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final ExpenseListAdapter adapter = new ExpenseListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


//        recyclerView.addOnItemTouchListener(
//                new RecyclerItemClickListener(this, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
//                    @Override public void onItemClick(View view, int position) {
//                        if( view.findViewById(R.id.notes).getVisibility()==View.GONE)
//                        view.findViewById(R.id.notes).setVisibility(View.VISIBLE);
//                        else
//                            view.findViewById(R.id.notes).setVisibility(View.GONE);
//
//                    }
//
//                    @Override public void onLongItemClick(View view, int position) {
//                        // do whatever
//                    }
//                })
//        );


        // Get a new or existing ViewModel from the ViewModelProvider.
        mExpenseViewModel = ViewModelProviders.of(this).get(ExpenseViewModel.class);

        // Add an observer on the LiveData returned by getAlphabetizedWords.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.

        mExpenseViewModel.getAllExpenses().observe(this,new Observer<List<Expense>>() {

            @Override
            public void onChanged(@Nullable final List<Expense> expenses) {
                // Update the cached copy of the categories in the adapter.

                adapter.setExpenses(expenses);
            }

        });
    }


}