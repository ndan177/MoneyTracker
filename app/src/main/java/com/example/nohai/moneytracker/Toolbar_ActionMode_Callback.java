package com.example.nohai.moneytracker;

import android.content.Context;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.nohai.moneytracker.Database.Debt;
import com.example.nohai.moneytracker.UI.Debts;

import java.util.ArrayList;

public class Toolbar_ActionMode_Callback implements ActionMode.Callback {

    private Context context;
    private DebtListAdapter DebtListAdapter;
    private ArrayList<Debt> message_models;

    public Toolbar_ActionMode_Callback(Context context, DebtListAdapter DebtListAdapter, ArrayList<Debt> message_models) {
        this.context = context;
        this.DebtListAdapter = DebtListAdapter;
        this.message_models = message_models;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.menu_action_mode, menu);//Inflate the menu over action mode
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

            menu.findItem(R.id.action_delete).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);



        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
//
//                Fragment recyclerFragment =  new Debts().getFragment(0);//Get recycler view fragment
//
//                    if (recyclerFragment != null)
//                        //If recycler fragment not null
//                        ((DebtsToPayFragment) recyclerFragment).deleteRows();//delete selected rows

                break;


        }
        return false;
    }


    @Override
    public void onDestroyActionMode(ActionMode mode) {

        //When action mode destroyed remove selected selections and set action mode to null
        //First check current fragment action mode

            DebtListAdapter.removeSelection();  // remove selection
//            Fragment recyclerFragment = new Debts().getFragment(0);//Get recycler fragment
//            if (recyclerFragment != null)
//                ((DebtsToPayFragment) recyclerFragment).setNullToActionMode();//Set action mode null

    }
}