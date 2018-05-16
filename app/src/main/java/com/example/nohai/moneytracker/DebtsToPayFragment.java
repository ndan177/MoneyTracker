package com.example.nohai.moneytracker;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.example.nohai.moneytracker.Database.Debt;
import com.example.nohai.moneytracker.UI.NewBorrowTo;
import com.example.nohai.moneytracker.UI.NewExpense;
import com.example.nohai.moneytracker.Utils.RecyclerItemClickListener;


import java.util.Date;
import java.util.List;

public class DebtsToPayFragment extends Fragment{
    public   DebtListAdapter adapter;
    private DebtViewModel mDebtViewModel;
    private ActionMode mActionMode;
    //Constructor default
    public DebtsToPayFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View PageOne = inflater.inflate(R.layout.fragment_debts_to_pay, container, false);
        final RecyclerView  recyclerView = PageOne.findViewById(R.id.recyclerview);
        adapter = new DebtListAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        if (mActionMode != null)
                            onListItemSelect(position);
                        int categoryId = mDebtViewModel.getAllDebtsTo().getValue().get(position).getId();

                        view.setSelected(true);
                        Intent intent = new Intent(getActivity(),NewBorrowTo.class);
                        intent.putExtra("id",categoryId);
                        intent.putExtra("position",position);

                        startActivity(intent);
                    }
                    @Override public void onLongItemClick(View view, int position) {
                        //Select item on long click
                        onListItemSelect(position);

                        Toast.makeText(getActivity(), "LONG", Toast.LENGTH_SHORT).show();
                    }
                })
        );

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
    //List item select method
    private void onListItemSelect(int position) {

        boolean hasCheckedItems = adapter.getSelectedCount() > 0;//Check if any items are already selected or not
        mActionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(new Toolbar_ActionMode_Callback(getActivity(),adapter, null));

        if (hasCheckedItems && mActionMode == null)
            // there are some selected items, start the actionMode
            mActionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(new Toolbar_ActionMode_Callback(getActivity(),adapter, null));
     //   else if (!hasCheckedItems && mActionMode != null)
            // there no selected items, finish the actionMode
         //   mActionMode.finish();

        if (mActionMode != null)
            //set action mode title on item selection
            mActionMode.setTitle(String.valueOf(adapter
                    .getSelectedCount()) + " selected");


    }
    //Set action mode null after use
    public void setNullToActionMode() {
        if (mActionMode != null)
            mActionMode = null;
    }

    //Delete selected rows
    public void deleteRows() {
        SparseBooleanArray selected = adapter
                .getSelectedIds();//Get selected ids

        //Loop all selected ids
        for (int i = (selected.size() - 1); i >= 0; i--) {
            if (selected.valueAt(i)) {
                //If current id is selected remove the item via key
               // mDebtViewModel.remove(selected.keyAt(i));
                adapter.notifyDataSetChanged();//notify adapter

            }
        }
        Toast.makeText(getActivity(), selected.size() + " item deleted.", Toast.LENGTH_SHORT).show();//Show Toast
        mActionMode.finish();//Finish action mode after use

    }


}

