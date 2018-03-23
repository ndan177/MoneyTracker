package com.example.nohai.moneytracker;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;


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
    static EditText dob;
    Category cat;
    int categoryId;
    TextView expenses;
    AppDatabase db;



    public static String getMonthName(int month) {

        return new DateFormatSymbols().getMonths()[month-1];
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
            dob.setText(day+"-"+month_name+"-"+year);
        }

    }
    //Constructor default
    public Page_1(){};


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View PageOne = inflater.inflate(R.layout.page1, container, false);
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        DateFormat dformat =  new SimpleDateFormat("yyyy-MM-dd");//for expenses

        db = Room.databaseBuilder(getActivity().getApplicationContext(), AppDatabase.class,"Database")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();


        String reportDate = dformat.format(c);
        double mySum=db.expenseDao().getPriceSum(reportDate);
        expenses=PageOne.findViewById(R.id.expenses);
        expenses.setText(String.valueOf(mySum));

        dob = PageOne.findViewById(R.id.dob);
        dob.setText(df.format(c.getTime()));
        dob.setOnClickListener(new View.OnClickListener() {

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
                        //Toast.makeText(getActivity(), String.valueOf(categoryId), Toast.LENGTH_SHORT).show();
                       // cat=new Category("zzzz");
                       // mCategoryViewModel.insert(cat);

                          Intent intent = new Intent(getActivity(), NewExpense.class);
                          intent.putExtra("id",String.valueOf(categoryId));
                           startActivity(intent);
                        //Toast.makeText(getActivity(), position+" ", Toast.LENGTH_SHORT).show();
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
}