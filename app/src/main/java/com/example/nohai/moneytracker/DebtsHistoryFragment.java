package com.example.nohai.moneytracker;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.persistence.room.Room;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.nohai.moneytracker.Database.Debt;
import com.example.nohai.moneytracker.UI.Debts;

import java.util.List;

public class DebtsHistoryFragment extends Fragment{
    public   DebtListAdapter adapter;
    private DebtViewModel mDebtViewModel;
    AppDatabase db;
    ConstraintLayout constraintLayout;
    //Constructor default
    public DebtsHistoryFragment(){db= Debts.db;}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View PageThree = inflater.inflate(R.layout.fragment_debts_history, container, false);
        final RecyclerView recyclerView = PageThree.findViewById(R.id.recyclerview);
        adapter = new DebtListAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        constraintLayout= PageThree.findViewById(R.id.constraint);

        // Get a new or existing ViewModel from the ViewModelProvider.
        mDebtViewModel = ViewModelProviders.of(this).get(DebtViewModel.class);

        //((Debts)getActivity()).loadViewPager();
        mDebtViewModel.getResolvedDebts().observe(this, new Observer<List<Debt>>() {

            @Override
            public void onChanged(@Nullable final List<Debt> debts) {
                // Update the cached copy of the categories in the adapter.
                adapter.setDebts(debts);
            }

        });

        // Add an observer on the LiveData returned by getAlphabetizedDebts.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.


        return PageThree;
    }
    @Override
    public void onResume() {
        super.onResume();
        List<Debt> debts= db.debtDao().getResolvedDebts2();
       if(debts.size()==0){constraintLayout.setVisibility(View.INVISIBLE);}
       else {constraintLayout.setVisibility(View.VISIBLE);}
        adapter.setDebts(debts);
        adapter.notifyDataSetChanged();

    }

}
