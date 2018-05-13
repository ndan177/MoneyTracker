package com.example.nohai.moneytracker;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.example.nohai.moneytracker.Database.Debt;


import java.util.Date;
import java.util.List;

public class DebtsToPayFragment extends Fragment{
    public   DebtListAdapter adapter;
    private DebtViewModel mDebtViewModel;

    //Constructor default
    public DebtsToPayFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View PageOne = inflater.inflate(R.layout.fragment_debts_to_pay, container, false);
        final RecyclerView  recyclerView = PageOne.findViewById(R.id.recyclerview);
        adapter = new DebtListAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // Get a new or existing ViewModel from the ViewModelProvider.
        mDebtViewModel = ViewModelProviders.of(this).get(DebtViewModel.class);

        //((Debts)getActivity()).loadViewPager();
        mDebtViewModel.getAllDebtsTo().observe(this, new Observer<List<Debt>>() {

            @Override
            public void onChanged(@Nullable final List<Debt> debts) {
                // Update the cached copy of the categories in the adapter.
                adapter.setDebts(debts);
            }

        });

        // Add an observer on the LiveData returned by getAlphabetizedDebts.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.

        return PageOne;
    }
    @Override
    public void onResume() {
        super.onResume();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        Debt debt = new Debt();
        debt.contactId= data.getIntExtra("contactId",-1);
        debt.price= data.getDoubleExtra("price",-1);
        debt.notes= data.getStringExtra("notes");
        debt.date = (Date)data.getSerializableExtra("date");
        debt.dateLimit = (Date)data.getSerializableExtra("dateLimit");
        if( debt.contactId!=-1 && debt.price!=-1 )
        mDebtViewModel.insert(debt);
        Toast.makeText(getActivity(), "Debt added!", Toast.LENGTH_SHORT).show();
    }


}

