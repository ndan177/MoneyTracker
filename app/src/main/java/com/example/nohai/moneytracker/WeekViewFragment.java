package com.example.nohai.moneytracker;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class WeekViewFragment extends Fragment {
    AppDatabase db;

    //Constructor default
    public WeekViewFragment(){};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View PageTwo = inflater.inflate(R.layout.page2, container, false);





        return PageTwo;
    }
}