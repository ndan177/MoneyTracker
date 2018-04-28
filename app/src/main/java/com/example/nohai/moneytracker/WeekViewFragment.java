package com.example.nohai.moneytracker;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.persistence.room.Room;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.nohai.moneytracker.Database.Category;
import com.example.nohai.moneytracker.UI.MainActivity;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class WeekViewFragment extends Fragment {
    private CategoryViewModel mCategoryViewModel;
    private TextView expenses;
    private TextView incomes;
    private AppDatabase db;
    private CategoryListAdapter adapter;
    static String dateChooser;
    private TextView  currencyText;
    private TextView  currencyText2;
    private TextView  startWeek;
    private TextView endWeek;

    //Constructor default
    public WeekViewFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View PageTwo = inflater.inflate(R.layout.page2, container, false);

        DateFormat dbDateFormat =  new SimpleDateFormat("yyyy-MM-dd");//for expenses and incomes

        db = Room.databaseBuilder(getActivity().getApplicationContext(), AppDatabase.class,"Database")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();

        currencyText = PageTwo.findViewById(R.id.currencyText);
        currencyText2 = PageTwo.findViewById(R.id.currencyText2);
        startWeek = PageTwo.findViewById(R.id.startWeek);
        endWeek = PageTwo.findViewById(R.id.endWeek);

        try
        {
            dateChooser = ((MainActivity) getActivity()).getSharedDate();
            startWeek.setText(displayDateFormat(firstDay(dateChooser)));
            endWeek.setText(displayDateFormat(lastDay(dateChooser)));
        }catch(Exception ex){}

        String dbDateStringStart = dbDateFormat.format(firstDay(dateChooser));
        String dbDateStringEnd = dbDateFormat.format(lastDay(dateChooser));
        double expensesSum = db.expenseDao().getPriceSumBetween(dbDateStringStart,dbDateStringEnd);
        double incomesSum = db.incomeDao().getPriceSumBetween(dbDateStringStart,dbDateStringEnd);

        expenses = PageTwo.findViewById(R.id.expenses);
        expenses.setText(String.valueOf(expensesSum));

        incomes = PageTwo.findViewById(R.id.incomes);
        incomes.setText(String.valueOf(incomesSum));

        final RecyclerView recyclerView = PageTwo.findViewById(R.id.recyclerview);
        adapter = new CategoryListAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),4));

        // Get a new or existing ViewModel from the ViewModelProvider.
        mCategoryViewModel = ViewModelProviders.of(this).get(CategoryViewModel.class);

        // Add an observer on the LiveData returned by getAllCategories.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        return PageTwo;
    }

    public void onResume() {
        DateFormat dateformat =  new SimpleDateFormat("yyyy-MM-dd");//for expenses
        String myDate = dateChooser;
        String startDate= dbDateFormat(firstDay(myDate));
        String lastDate= dbDateFormat(lastDay(myDate));

        Date date1;
        Date date2;

        try {
            date1 = new SimpleDateFormat("dd-MMM-yyyy").parse(startDate);
            String reportDateFirst = dateformat.format( date1);
            date2 = new SimpleDateFormat("dd-MMM-yyyy").parse(lastDate);
            String reportDateLast = dateformat.format( date2);

            double mySum = db.expenseDao().getPriceSumBetween(reportDateFirst,reportDateLast);
            expenses.setText(String.format ("%.2f",mySum));
            double mySumIncome=db.incomeDao().getPriceSumBetween(reportDateFirst,reportDateLast);
            incomes.setText(String.format ("%.2f",mySumIncome));

        }catch(Exception Ex){}

        try{
            mCategoryViewModel.getAllCategories().observe(this,new Observer<List<Category>>() {

                @Override
                public void onChanged(@Nullable final List<Category> categories) {
                    // Update the cached copy of the categories in the adapter.
                    for(int i = 0; i < categories.size(); i++)
                    {
                        DateFormat dateformat =  new SimpleDateFormat("yyyy-MM-dd");//for expenses
                        String myDate= dateChooser;
                        String startDate= dbDateFormat(firstDay(myDate));
                        String lastDate= dbDateFormat(lastDay(myDate));
                        Date date1;
                        Date date2;

                        try {
                            date1 = new SimpleDateFormat("dd-MMM-yyyy").parse(startDate);
                            String reportDateFirst = dateformat.format( date1 );
                            date2 = new SimpleDateFormat("dd-MMM-yyyy").parse(lastDate);
                            String reportDateLast = dateformat.format( date2);
                            double mySum = db.expenseDao().
                                    getPriceSumBetweenByCategory
                                            (categories.get(i).getId(),reportDateFirst,reportDateLast);
                            categories.get(i).expensesCost = mySum;
                        }catch( Exception Ex ) {}
                    }
                    adapter.setCategories(categories);
                }
            });
        }catch (Exception Ex ){}

        loadCurrency();
        super.onResume();
    }
    void loadCurrency()
    {
        String myCurrency = ((MainActivity)getActivity()).getCurrency();
        currencyText.setText(myCurrency);
        currencyText2.setText(myCurrency);
    }

    Date firstDay(String stringDate)//return first day from week, date format
    {
        Date date;
        Date weekStart=null;
        try {
            date =new SimpleDateFormat("dd-MMM-yyyy").parse(stringDate);

            Calendar c = Calendar.getInstance();
            c.setTime(date);
            int dayOfWeek = c.get(Calendar.DAY_OF_WEEK) - c.getFirstDayOfWeek();//sunday
            c.add(Calendar.DAY_OF_MONTH, -dayOfWeek);
            weekStart = c.getTime();

        }catch(Exception Ex){}

        return weekStart;
   }
    Date lastDay(String stringDate)//return last day from week, date format
    {
        Date date;
        Date weekEnd=null;
        try {
            date =new SimpleDateFormat("dd-MMM-yyyy").parse(stringDate);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            int dayOfWeek = c.get(Calendar.DAY_OF_WEEK) - c.getFirstDayOfWeek();//sunday
            c.add(Calendar.DAY_OF_MONTH, -dayOfWeek);
            c.add(Calendar.DAY_OF_MONTH, 6);
            weekEnd = c.getTime();
        }catch(Exception Ex){}

        return weekEnd;
    }

    String displayDateFormat(Date date)
    {
        return new SimpleDateFormat("dd MMMM").format(date);
    }
    String dbDateFormat(Date date)
    {
        return new SimpleDateFormat("dd-MMM-yyyy").format(date);
    }
}