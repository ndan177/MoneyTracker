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

public class YearViewFragment extends Fragment {

    private CategoryViewModel mCategoryViewModel;
    private TextView expenses;
    private TextView incomes;
    private AppDatabase db;
    private CategoryListAdapter adapter;
    static String dateChooser;
    private TextView  currencyText;
    private TextView  currencyText2;
    private TextView  currencyText3;
    private TextView year;
    private TextView  balanceText;
    private TextView  balanceSum;

    //Constructor default
    public YearViewFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View PageFour = inflater.inflate(R.layout.page4, container, false);

        DateFormat dbDateFormat =  new SimpleDateFormat("yyyy-MM-dd");//for expenses and incomes

        db = Room.databaseBuilder(getActivity().getApplicationContext(), AppDatabase.class,"Database")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();

        currencyText = PageFour.findViewById(R.id.currencyText);
        currencyText2 = PageFour.findViewById(R.id.currencyText2);
        currencyText3 = PageFour.findViewById(R.id.currencyText3);
        year = PageFour.findViewById(R.id.date);

        try
        {
            dateChooser = ((MainActivity) getActivity()).getSharedDate();
            year.setText(displayDateFormat(dayDateToString(dateChooser)));

        }catch(Exception ex){}

        String dbDateStringStart = dbDateFormat.format(dayDateToString(dateChooser));

        double expensesSum = db.expenseDao().getPriceSumForYear(dbDateStringStart);
        double incomesSum = db.incomeDao().getPriceSumForYear(dbDateStringStart);

        expenses = PageFour.findViewById(R.id.expenses);
        expenses.setText(String.valueOf(expensesSum));

        incomes = PageFour.findViewById(R.id.incomes);
        incomes.setText(String.valueOf(incomesSum));

        double balance = incomesSum-expensesSum;

        balanceText= PageFour.findViewById(R.id.balance);
        balanceSum = PageFour.findViewById(R.id.balanceSum);
        balanceSum.setText(String.format ("%.2f",expensesSum-incomesSum));

        if(balance>=0)
            MainActivity.setBalanceColorGreen(balanceSum,currencyText3,balanceText);
        else
            MainActivity.setBalanceColorRed(balanceSum,currencyText3,balanceText);


        final RecyclerView recyclerView = PageFour.findViewById(R.id.recyclerview);
        adapter = new CategoryListAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(),4));

        // Get a new or existing ViewModel from the ViewModelProvider.
        mCategoryViewModel = ViewModelProviders.of(this).get(CategoryViewModel.class);

        // Add an observer on the LiveData returned by getAllCategories.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.

        return PageFour;
    }

    public void onResume() {
        DateFormat dateformat =  new SimpleDateFormat("yyyy-MM-dd");//for expenses
        String myDate = dateChooser;
        String startDate= dbDateFormat(dayDateToString(myDate));
        Date date1;

        try {
            date1 = new SimpleDateFormat("dd-MMM-yyyy").parse(startDate);
            String reportDateFirst = dateformat.format( date1);

            double mySum = db.expenseDao().getPriceSumForYear(reportDateFirst);
            expenses.setText(String.format ("%.2f",mySum));
            double mySumIncome=db.incomeDao().getPriceSumForYear(reportDateFirst);
            incomes.setText(String.format ("%.2f",mySumIncome));

            double balance = mySumIncome-mySum;
            balanceSum.setText(String.format ("%.2f",balance));
            if(balance >= 0)
                MainActivity.setBalanceColorGreen(balanceSum,currencyText3,balanceText);
            else
                MainActivity.setBalanceColorRed(balanceSum,currencyText3,balanceText);

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
                        String startDate= dbDateFormat(dayDateToString(myDate));
                        Date date1;

                        try {
                            date1 = new SimpleDateFormat("dd-MMM-yyyy").parse(startDate);
                            String reportDateFirst = dateformat.format( date1 );

                            double mySum = db.expenseDao().
                                    getPriceSumForYearByCategory
                                            (categories.get(i).getId(),reportDateFirst);
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
        currencyText3.setText(myCurrency);
    }

    Date dayDateToString(String stringDate)//return first day from week, date format
    {
        Date date;
        Date day=null;
        try {
            date =new SimpleDateFormat("dd-MMM-yyyy").parse(stringDate);

            Calendar c = Calendar.getInstance();
            c.setTime(date);
            day = c.getTime();
        }catch(Exception Ex){}
        return day;
    }

    String displayDateFormat(Date date)
    {
        return new SimpleDateFormat("yyyy").format(date);
    }
    String dbDateFormat(Date date)
    {
        return new SimpleDateFormat("dd-MMM-yyyy").format(date);
    }
}