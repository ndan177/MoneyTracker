package com.example.nohai.moneytracker;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.GetChars;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.nohai.moneytracker.Database.Category;
import com.example.nohai.moneytracker.UI.NewExpense;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.text.DateFormatSymbols;
import java.util.List;

public class Page_1 extends Fragment {
    private CategoryViewModel mCategoryViewModel;
    static EditText dateChooser;
    int categoryId;
    TextView expenses;
    AppDatabase db;
    static int fragmentLoadedCounter =0;

    public static String getMonthName(int month) {

        return new DateFormatSymbols().getShortMonths()[month-1];
    }

    public static class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar calendar = Calendar.getInstance();
            int yy = calendar.get(Calendar.YEAR);
            int mm = calendar.get(Calendar.MONTH);
            int dd = calendar.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, yy, mm, dd);
        }


        public void onDateSet(DatePicker view, int yy, int mm, int dd) {
            populateSetDate(yy, mm+1, dd);


        }
        public void populateSetDate(int year, int month, int day) {
            String month_name=getMonthName(month);
            dateChooser.setText(day+"-"+month_name+"-"+year);

        }


    }
    //Constructor default
    public Page_1(){};


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View PageOne = inflater.inflate(R.layout.page1, container, false);
        Date currentDay = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        DateFormat dbDateFormat =  new SimpleDateFormat("yyyy-MM-dd");//for expenses

        db = Room.databaseBuilder(getActivity().getApplicationContext(), AppDatabase.class,"Database")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();

        String dbDateString = dbDateFormat.format(currentDay);
        double expensesSum=db.expenseDao().getPriceSum(dbDateString);

        expenses=PageOne.findViewById(R.id.expenses);
        expenses.setText(String.valueOf(expensesSum));

        dateChooser = PageOne.findViewById(R.id.date);
        dateChooser.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                onResume();
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

       if(fragmentLoadedCounter ==0) { //if it is the first time, set current day
           dateChooser.setText(simpleDateFormat.format(currentDay.getTime()));//set current day
            fragmentLoadedCounter =1;
       }
       else  //set saved date
       {
           SharedPreferences sharedPref = getActivity().getSharedPreferences("DATE",Context.MODE_PRIVATE);
           String defaultValue = getResources().getString(R.string.saved_date);
           String myDate = sharedPref.getString(getString(R.string.saved_date), defaultValue);
            dateChooser.setText(myDate);
       }

        dateChooser.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                DialogFragment newFragment = new SelectDateFragment();

                newFragment.show(getFragmentManager(), "DatePicker");
            }
        });


        RecyclerView recyclerView = PageOne.findViewById(R.id.recyclerview);
        final CategoryListAdapter adapter = new CategoryListAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),4));



        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        categoryId=mCategoryViewModel.getAllCategories().getValue().get(position).getId();

                        SharedPreferences sharedPref = getActivity().getSharedPreferences("DATE",Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(getString(R.string.saved_date), dateChooser.getText().toString());
                        editor.commit();

                          Intent intent = new Intent(getActivity(), NewExpense.class);
                          intent.putExtra("id",String.valueOf(categoryId));
                              // TODO: Pass date through
                          intent.putExtra("date",dateChooser.getText().toString());
                           startActivity(intent);

                    }

                    @Override public void onLongItemClick(View view, int position) {
                        // do whatever
                }
                })
        );



        // Get a new or existing ViewModel from the ViewModelProvider.
        mCategoryViewModel = ViewModelProviders.of(this).get(CategoryViewModel.class);

        // Add an observer on the LiveData returned by getAlphabetizedWords.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        mCategoryViewModel.getAllCategories().observe(this, new Observer<List<Category>>() {
            @Override
            public void onChanged(@Nullable final List<Category> categories) {
                // Update the cached copy of the words in the adapter.
                adapter.setCategories(categories);
            }
        });

        return PageOne;
    }
    @Override
    public void onResume() {

        DateFormat dateformat =  new SimpleDateFormat("yyyy-MM-dd");//for expenses
        String myDate= dateChooser.getText().toString();
        Date date1;
        try {
            date1 = new SimpleDateFormat("dd-MMM-yyyy").parse(myDate);
            String reportDate = dateformat.format( date1);
            double mySum=db.expenseDao().getPriceSum(reportDate);
            expenses.setText(String.valueOf(mySum));
        }catch(Exception Ex){}

        super.onResume();
    }



}