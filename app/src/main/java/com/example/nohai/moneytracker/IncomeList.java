package com.example.nohai.moneytracker;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.persistence.room.Room;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.nohai.moneytracker.Database.Category;
import com.example.nohai.moneytracker.Database.Income;
import com.example.nohai.moneytracker.Database.Income;
import com.example.nohai.moneytracker.UI.MainActivity;
import com.example.nohai.moneytracker.Utils.DateHelper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class IncomeList extends AppCompatActivity {

    private IncomeViewModel mIncomeViewModel;
    public  IncomeListAdapter adapter;
    private RecyclerView recyclerView;
    private AppDatabase db;
    private int type;
    private String day;
    private String weekStart;
    private String weekEnd;
    DateFormat dbDateFormat;
    SimpleDateFormat chooserDateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income_list);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("Incomes");
        actionbar.setDisplayHomeAsUpEnabled(true);

        db = Room.databaseBuilder(this, AppDatabase.class,"Database")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();

        dbDateFormat =  new SimpleDateFormat("yyyy-MM-dd");
        chooserDateFormat = new SimpleDateFormat("dd-MMM-yyyy");

        day = getIntent().getStringExtra("day");
        weekStart = getIntent().getStringExtra("weekStart");
        weekEnd = getIntent().getStringExtra("weekEnd");

        recyclerView = findViewById(R.id.recyclerview);
        adapter = new IncomeListAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        type = getIntent().getIntExtra("type",0);
        //0 is All
        if(day!=null) {
            weekStart = dbDateFormat.format(DateHelper.firstWeekDay(day));
            weekEnd = dbDateFormat.format(DateHelper.lastWeekDay(day));
        }

    }

    @Override
    public void onResume() {
        try{
            switch (type)
            {
                //all
                case 0:
                    mIncomeViewModel = ViewModelProviders.of(this).get(IncomeViewModel.class);
                    mIncomeViewModel.getAllIncomes().observe(this,new Observer<List<Income>>() {
                        @Override
                        public void onChanged(@Nullable final List<Income> incomes) {

                            adapter.setIncomes(incomes);
                        }
                    });
                    break;

                //day
                case 1:

                    Date parsed = chooserDateFormat.parse(day);
                    day = dbDateFormat.format(parsed);

                    mIncomeViewModel = ViewModelProviders.of(this).get(IncomeViewModel.class);
                    mIncomeViewModel.getAllIncomes().observe(this,new Observer<List<Income>>() {
                        @Override
                        public void onChanged(@Nullable final List<Income> income) {

                            adapter.setIncomes(db.incomeDao().getDayIncomes(day));
                        }
                    });
                    break;

                //week
                case 2:

                    mIncomeViewModel = ViewModelProviders.of(this).get(IncomeViewModel.class);
                    mIncomeViewModel.getAllIncomes().observe(this,new Observer<List<Income>>() {
                        @Override
                        public void onChanged(@Nullable final List<Income> incomes) {

                            adapter.setIncomes(db.incomeDao().getWeekIncomes(weekStart,weekEnd));
                        }
                    });
                    break;

                //month
                case 3:
                    Date parsedDate = chooserDateFormat.parse(day);
                    day = dbDateFormat.format(parsedDate);

                    mIncomeViewModel = ViewModelProviders.of(this).get(IncomeViewModel.class);
                    mIncomeViewModel.getAllIncomes().observe(this,new Observer<List<Income>>() {
                        @Override
                        public void onChanged(@Nullable final List<Income> Incomes) {

                            adapter.setIncomes(db.incomeDao().getMonthIncomes(day));
                        }
                    });
                    break;

                //year
                case 4:
                    Date parsedDateForYear = chooserDateFormat.parse(day);
                    day = dbDateFormat.format(parsedDateForYear);

                    mIncomeViewModel = ViewModelProviders.of(this).get(IncomeViewModel.class);
                    mIncomeViewModel.getAllIncomes().observe(this,new Observer<List<Income>>() {
                        @Override
                        public void onChanged(@Nullable final List<Income> incomes) {

                            adapter.setIncomes(db.incomeDao().getYearIncomes(day));
                        }
                    });
                    break;
            }

        }catch (Exception Ex ){}

        super.onResume();
    }

}
