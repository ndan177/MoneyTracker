package com.example.nohai.moneytracker.UI;

import android.arch.persistence.room.Room;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.nohai.moneytracker.AppDatabase;
import com.example.nohai.moneytracker.Database.Expense;
import com.example.nohai.moneytracker.Database.Income;
import com.example.nohai.moneytracker.R;
import com.example.nohai.moneytracker.Utils.DateHelper;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


public class ProfitChart extends AppCompatActivity  implements AdapterView.OnItemSelectedListener {
    AppDatabase db;
    List<Income> incomes;
    List<Expense> expenses;
    ArrayList<BarEntry> barEntries = new ArrayList<>();
    ArrayList<BarEntry> barEntries2 =new ArrayList<>();
    ArrayList<BarEntry> barEntries3 =new ArrayList<>();
    BarChart barChart;
    String[] months;
    Spinner spinner;
    static int counter;


    protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_chart);
        spinner=findViewById(R.id.spinner);
        setToolbar();
        counter = 0;
        db = Room.databaseBuilder(this.getApplicationContext(), AppDatabase.class,"Database")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();

        expenses = db.expenseDao().getExpenses();
        incomes = db.incomeDao().getIncomes();

        barChart = findViewById(R.id.barChart);
        setBarChart();// common specifications
        months = loadMonths();// all months in system

//        for(int i=0; i < months.length;i++)
//            setMonthDataForCompare(months[i],i);//load expenses and incomes, for compare
       loadSpinnerData();
        spinner.setOnItemSelectedListener(this);


    }
    public class MyAxisValueFormatter implements IAxisValueFormatter{
        String[] mValues;
        public MyAxisValueFormatter(String[] values) {
            this.mValues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            if (value >= 0) {
                if (mValues.length > (int) value) {
                    return mValues[(int) value];
                } else return "";
            } else {
                return "";
            }
        }
    }

    private String[] loadMonths() {

        List<String> categoriesNames = new ArrayList<>();
        List<Date> dates = new ArrayList<>();

        Calendar c = Calendar.getInstance();

        for (Expense expense : expenses) {
            if (!dates.contains(expense.date))
                dates.add(expense.date);
        }
        for (Income income : incomes) {
            if (!dates.contains(income.date))
                dates.add(income.date);
        }

        Collections.sort(dates, new Comparator<Date>() {
            public int compare(Date o1, Date o2) {
                if (o1 == null || o2 == null)
                    return 0;
                return o1.compareTo(o2);
            }
        });
        for(int i=0;i<dates.size();i++)
        {
            c.setTime(dates.get(i));
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH) + 1;
            String aux = DateHelper.getMonthName(month) + "-" + year;
            if (!categoriesNames.contains(aux))
                categoriesNames.add(aux);
        }


        return categoriesNames.toArray(new String[0]);
    }
    void setMonthDataForCompare(String month, int i)
    {

        DateFormat dateformat =  new SimpleDateFormat("yyyy-MM-dd");//for expenses

        String myDate = month;
        Date date1;
        String reportDate = "";

        try {
            date1 = new SimpleDateFormat("dd-MMM-yyyy").parse("10-"+myDate);
            reportDate= dateformat.format( date1 );

        }catch( Exception Ex ) {}

        double mySum = db.expenseDao().
                getPriceSumForMonth(reportDate);
        barEntries.add(new BarEntry(i,(float)mySum));

        double mySumIncome = db.incomeDao().
                getPriceSumForMonth(reportDate);

        barEntries2.add(new BarEntry(i,(float)mySumIncome));
        ///

        BarDataSet barDataSet = new BarDataSet(barEntries, "data set1");
        barDataSet.setColor((getColor(R.color.red)));

        BarDataSet barDataSet2 = new BarDataSet(barEntries2, "data set2");
        barDataSet2.setColor((getColor(R.color.colorAccent)));

        BarData data = new BarData(barDataSet, barDataSet2);

        float groupSpace = 0.1f;
        float barSpace = 0.02f;
        float barWidth = 0.43f;
        data.setBarWidth(barWidth);

        barChart.setData(data);
//        barChart.setDrawGridBackground(false);
//        barChart.setVisibleXRangeMaximum(10);
        barChart.groupBars(0, groupSpace, barSpace);
        Description description = new Description();
        description.setText("Expenses and Incomes");
        barChart.setDescription(description);

    }

    void setMonthDataProfit(String month,int i)
    {
        DateFormat dateformat =  new SimpleDateFormat("yyyy-MM-dd");//for expenses

        String myDate = month;
        Date date1;
        String reportDate = "";

        try {
            date1 = new SimpleDateFormat("dd-MMM-yyyy").parse("10-"+myDate);
            reportDate= dateformat.format( date1 );

        }catch( Exception Ex ) {}

        double mySum = db.expenseDao().
                getPriceSumForMonth(reportDate);


        double mySumIncome = db.incomeDao().
                getPriceSumForMonth(reportDate);

        double diff = mySumIncome - mySum;
        BarDataSet barDataSet3 = new BarDataSet(barEntries3,"data set3");

        barDataSet3.setColor((getColor(R.color.colorAccent)));



        barEntries3.add(new BarEntry(i,(float)diff));

        BarData data = new BarData(barDataSet3,barDataSet3);

        barChart.setData(data);
        float groupSpace = 0.1f;
        float barSpace = 0.02f;
        float barWidth = 0.43f;
        data.setBarWidth(barWidth);
        barChart.groupBars(0, groupSpace, barSpace);
        Description description = new Description();
        description.setText("Savings");
        barChart.setDescription(description);


    }

    void setBarChart(){

        barChart.setDrawValueAboveBar(true);
        barChart.setMaxVisibleValueCount(50);
        barChart.setPinchZoom(false);
        barChart.setFitBars(true);
        barChart.setPinchZoom(true);
        //  barChart.setDrawGridBackground(false);
        barChart.getLegend().setEnabled(false);
        barChart.getAxisLeft().setDrawLabels(true);
        barChart.getAxisRight().setDrawLabels(false);
        barChart.animateY(1000, Easing.EasingOption.EaseInCubic);
//        barChart.setFitBars(true);
        //barChart.setVerticalScrollBarEnabled(true);

    }
    void setAxis()
    {
        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setDrawGridLines(false);

        yAxis = barChart.getAxisRight();
        yAxis.setDrawGridLines(false);


        XAxis xAxis =  barChart.getXAxis();
        xAxis.setValueFormatter(new MyAxisValueFormatter(months));
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        xAxis.setGranularity(1);
        xAxis.setCenterAxisLabels(true);
        xAxis.setAxisMinimum(0);
        xAxis.setAxisMaximum(barChart.getXChartMax()+1);
    }
    private void loadSpinnerData()
    {

        List<String> categoriesNames=new ArrayList<>();
        categoriesNames.add("Expenses and Incomes");
        categoriesNames.add("Savings");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_item, categoriesNames);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position,
    long id) {

                switch (position) {
                    case 0:
                        barEntries.clear();
                        barEntries2.clear();
                        barChart.clear();
                        setBarChart();
                        for(int i=0; i < months.length;i++)
                            setMonthDataForCompare(months[i],i);//load expenses and incomes, for compare
                        if(counter==0){setAxis();counter =99;}
                        else counter =99;

                        break;
                    case 1:
                        barEntries3.clear();
                        barChart.clear();
                        setBarChart();
                        for(int i=0; i < months.length;i++)
                            setMonthDataProfit(months[i],i);//load expenses and incomes, for compare

                        break;


        }

    }
    @Override
    public void onNothingSelected(AdapterView<?> arg0) {    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }
    //TODO: DESTROY ACTIVITY WHEN BACK KEY IS PRESSED
    private  void setToolbar(){
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("Profit Chart");
        actionbar.setDisplayHomeAsUpEnabled(true);
    }
}
