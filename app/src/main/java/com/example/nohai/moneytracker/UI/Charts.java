package com.example.nohai.moneytracker.UI;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.arch.persistence.room.Room;
import android.graphics.Color;
import android.opengl.Visibility;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.nohai.moneytracker.AppDatabase;
import com.example.nohai.moneytracker.Database.Category;
import com.example.nohai.moneytracker.Database.Expense;
import com.example.nohai.moneytracker.R;
import com.example.nohai.moneytracker.Utils.DateHelper;
import com.example.nohai.moneytracker.Utils.MyColorTemplate;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class Charts extends AppCompatActivity  implements AdapterView.OnItemSelectedListener {
    AppDatabase db;
    PieChart pieChart;
    List<Category> categories;
    Spinner spinner;
    Spinner monthSelect;
    Spinner  yearSelect;
    ArrayList<PieEntry> yValues;
    PieDataSet dataSet;
    PieData data;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
    Date currentDay = Calendar.getInstance().getTime();
    static TextView dateChooser;
    private TextView  startWeek;
    private TextView endWeek;

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
            String month_name = DateHelper.getMonthName(month);
            dateChooser.setText(day+"-"+month_name+"-"+year);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_charts);
        setToolbar();

        db = Room.databaseBuilder(this.getApplicationContext(), AppDatabase.class,"Database")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();

        dateChooser = findViewById(R.id.date);
        setDatChooserListener();

        startWeek = findViewById(R.id.startWeek);
        endWeek = findViewById(R.id.endWeek);

        categories = db.categoryDao().getCategories();

        spinner = findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);

        monthSelect = findViewById(R.id.monthSelect);
        monthSelect.setOnItemSelectedListener(this);

        yearSelect = findViewById(R.id.yearSelect);
        yearSelect.setOnItemSelectedListener(this);

        loadSpinnerData();
        loadSpinnerDataMonth();
        loadSpinnerDataYear();

        pieChart = findViewById(R.id.pieChart);
        setPieChart();

        yValues = new  ArrayList<>();

    }

    private void setPieChart(){
        pieChart.setUsePercentValues(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5,10,5,5);

        pieChart.setDragDecelerationFrictionCoef(0.99f);

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(getColor(R.color.veryLightGreen));
        pieChart.setTransparentCircleRadius(60f);

        pieChart.animateY(1000, Easing.EasingOption.EaseInCubic);
    }

    private void loadSpinnerData() {

        List<String> categoriesNames=new ArrayList<>();
        categoriesNames.add("day");
        categoriesNames.add("week");
        categoriesNames.add("month");
        categoriesNames.add("year");
        categoriesNames.add("all");


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, categoriesNames);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
    }
    private void loadSpinnerDataMonth() {
        List<Expense>   expenses = db.expenseDao().getExpenses();
        List<String> categoriesNames=new ArrayList<>();

        Calendar c = Calendar.getInstance();

        for(Expense expense : expenses) {
            c.setTime(expense.date);
            int year = c.get(Calendar.YEAR);
            int month= c.get(Calendar.MONTH)+1;
            String aux=DateHelper.getMonthName(month)+"-"+year;
            if(!categoriesNames.contains(aux))
                categoriesNames.add(aux);
        }

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, categoriesNames);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        monthSelect.setAdapter(dataAdapter);
    }

    private void loadSpinnerDataYear() {
        List<Expense> expenses= db.expenseDao().getExpenses();
        List<String> categoriesNames=new ArrayList<>();
        Calendar c = Calendar.getInstance();

        for(Expense expense : expenses) {
            c.setTime(expense.date);
            int year = c.get(Calendar.YEAR);
            String aux=""+year;
            if(!categoriesNames.contains(aux))
                categoriesNames.add(aux);
        }
        Collections.sort(categoriesNames, Collections.reverseOrder());

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, categoriesNames);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        yearSelect.setAdapter(dataAdapter);
    }
    void setDay()
    {
            setPieChart();
            DateFormat dateformat =  new SimpleDateFormat("yyyy-MM-dd");//for expenses
            String myDate = dateChooser.getText().toString();
            Date date1;
            String reportDate="";

            try {
                date1 = new SimpleDateFormat("dd-MMM-yyyy").parse(myDate);
                reportDate= dateformat.format( date1 );

            }catch( Exception Ex ) {}

        yValues.clear();

        for(int i=0;i<categories.size();i++)
        { double mySum = db.expenseDao().getPriceSumByCategory(categories.get(i).getId(),reportDate);
          if(mySum!=0)
              yValues.add(new PieEntry((float)mySum, categories.get(i).getCategory()));
        }
       repetitive();
    }

    void setWeek()
    {
        startWeek.setText(displayDateFormat(DateHelper.firstWeekDay(dateChooser.getText().toString())));
        endWeek.setText(displayDateFormat(DateHelper.lastWeekDay(dateChooser.getText().toString())));
        setPieChart();
        yValues.clear();
        try{
            DateFormat dateformat =  new SimpleDateFormat("yyyy-MM-dd");//for expenses
            String myDate= dateChooser.getText().toString();
            String startDate = dbDateFormat(DateHelper.firstWeekDay(myDate));
            String lastDate = dbDateFormat(DateHelper.lastWeekDay(myDate));
            Date date1;
            Date date2;

            for(int i = 0; i < categories.size(); i++)
            {   double mySum = 0;

                date1 = new SimpleDateFormat("dd-MMM-yyyy").parse(startDate);
                String reportDateFirst = dateformat.format( date1 );
                date2 = new SimpleDateFormat("dd-MMM-yyyy").parse(lastDate);
                String reportDateLast = dateformat.format( date2);
                mySum = db.expenseDao().
                        getPriceSumBetweenByCategory
                                (categories.get(i).getId(),reportDateFirst,reportDateLast);
                if(mySum!=0)
                    yValues.add(new PieEntry((float)mySum, categories.get(i).getCategory()));

            }

        }catch (Exception Ex ){}
        repetitive();

    }
    void setMonth()
    {

        setPieChart();
        DateFormat dateformat =  new SimpleDateFormat("yyyy-MM-dd");//for expenses
        String myDate = monthSelect.getSelectedItem().toString();
        Date date1;
        String reportDate="";

        try {
            date1 = new SimpleDateFormat("dd-MMM-yyyy").parse("10-"+myDate);
            reportDate= dateformat.format( date1 );

        }catch( Exception Ex ) {}

        yValues.clear();

        for(int i=0;i<categories.size();i++)
        { double mySum = db.expenseDao().
                getPriceSumForMonthByCategory
                        (categories.get(i).getId(),reportDate);
            if(mySum!=0)
                yValues.add(new PieEntry((float)mySum, categories.get(i).getCategory()));
        }
        repetitive();

    }
    void setYear()
    {
        setPieChart();
        String myDate = yearSelect.getSelectedItem().toString();

        yValues.clear();

        for(int i=0;i<categories.size();i++)
        { double mySum = db.expenseDao().getPriceSumForJustYearByCategory(categories.get(i).getId(),myDate);
            if(mySum!=0)
                yValues.add(new PieEntry((float)mySum, categories.get(i).getCategory()));
        }
        repetitive();

    }
    void setAllTime()
    {
        setPieChart();

        yValues.clear();

        for(int i=0;i<categories.size();i++)
        { double mySum = db.expenseDao().getAllTimeSumByCategory(categories.get(i).getId());
            if(mySum!=0)
                yValues.add(new PieEntry((float)mySum, categories.get(i).getCategory()));
        }
        repetitive();

    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,
                               long id) {
        switch(parent.getId()) {
            case R.id.spinner:
                switch (position) {
                    case 0:
                        setDay();
                        dateChooser.setVisibility(View.VISIBLE);
                        monthSelect.setVisibility(View.GONE);
                        yearSelect.setVisibility(View.GONE);
                        startWeek.setVisibility(View.GONE);
                        endWeek.setVisibility(View.GONE);
                        break;
                    case 1:
                        setWeek();
                        dateChooser.setVisibility(View.VISIBLE);
                        monthSelect.setVisibility(View.GONE);
                        yearSelect.setVisibility(View.GONE);
                        startWeek.setVisibility(View.VISIBLE);
                        endWeek.setVisibility(View.VISIBLE);
                        break;
                    case 2:
                        setMonth();
                        monthSelect.setVisibility(View.VISIBLE);
                        dateChooser.setVisibility(View.GONE);
                        yearSelect.setVisibility(View.GONE);
                        startWeek.setVisibility(View.GONE);
                        endWeek.setVisibility(View.GONE);
                        break;
                    case 3:
                        setYear();
                        dateChooser.setVisibility(View.INVISIBLE);
                        monthSelect.setVisibility(View.INVISIBLE);
                        yearSelect.setVisibility(View.VISIBLE);
                        startWeek.setVisibility(View.GONE);
                        endWeek.setVisibility(View.GONE);
                        break;
                    case 4:
                        setAllTime();
                        dateChooser.setVisibility(View.GONE);
                        monthSelect.setVisibility(View.GONE);
                        yearSelect.setVisibility(View.GONE);
                        startWeek.setVisibility(View.GONE);
                        endWeek.setVisibility(View.GONE);
                        break;
                }
                break;

            case R.id.monthSelect:
                setMonth();
                break;

            case R.id.yearSelect:
                 setYear();
                break;

        }

    }
    @Override
    public void onNothingSelected(AdapterView<?> arg0) {    }


    void repetitive()
    {
        dataSet = new PieDataSet(yValues,"");

        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(MyColorTemplate.JOYFUL_COLORS);

        data = new PieData(dataSet);

        data.setValueTextSize(10f);
        data.setValueTextColor(Color.YELLOW);

        pieChart.setData(data);
    }
    String dbDateFormat(Date date)
    {
        return new SimpleDateFormat("dd-MMM-yyyy").format(date);
    }
    private  void setToolbar(){
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("Charts");
        actionbar.setDisplayHomeAsUpEnabled(true);
    }
    private void setDatChooserListener()
    {
        dateChooser.setText(simpleDateFormat.format(currentDay.getTime()));
        dateChooser.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                DialogFragment newFragment = new Charts.SelectDateFragment();

                newFragment.show(getSupportFragmentManager(), "DatePicker");
            }
        });
        dateChooser.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(spinner.getSelectedItemPosition()==0)//day
                setDay();
                if(spinner.getSelectedItemPosition()==1)//week
                setWeek();

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });

    }
    String displayDateFormat(Date date)
    {
        return new SimpleDateFormat("dd MMMM").format(date);
    }

}
