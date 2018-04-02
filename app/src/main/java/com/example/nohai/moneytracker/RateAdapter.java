package com.example.nohai.moneytracker;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by nohai on 4/1/2018.
 */

public class RateAdapter extends ArrayAdapter<Rate> {

        public RateAdapter(Context context, ArrayList<Rate> rates) {
            super(context, 0, rates);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Rate rate = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.rate_item, parent, false);
            }
            // Lookup view for data population
            TextView value = convertView.findViewById(R.id.value);
            TextView name = convertView.findViewById(R.id.name);
            TextView multiplier = convertView.findViewById(R.id.multiplier);

            // Populate the data into the template view using the data object
            value.setText(rate.value);
            name.setText(rate.name);
            multiplier.setText(rate.multiplier);

            // Return the completed view to render on screen
            return convertView;
        }
    }