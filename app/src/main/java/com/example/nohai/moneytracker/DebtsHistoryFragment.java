package com.example.nohai.moneytracker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DebtsHistoryFragment extends Fragment{

    //Constructor default
    public DebtsHistoryFragment(){};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View PageTree = inflater.inflate(R.layout.fragment_debts_history, container, false);



        return PageTree;
    }
}
