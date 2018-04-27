package com.example.nohai.moneytracker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DebtsToReceiveFragment  extends Fragment{

    //Constructor default
    public DebtsToReceiveFragment(){};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View PageTree = inflater.inflate(R.layout.fragment_debts_to_receive, container, false);



        return PageTree;
    }
}
