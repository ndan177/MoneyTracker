package com.example.nohai.moneytracker.UI;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.arch.persistence.room.Room;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.davidmiguel.numberkeyboard.NumberKeyboard;
import com.davidmiguel.numberkeyboard.NumberKeyboardListener;
import com.example.nohai.moneytracker.AppDatabase;
import com.example.nohai.moneytracker.Database.Income;
import com.example.nohai.moneytracker.R;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static java.lang.Float.parseFloat;

public class NewIncome extends AppCompatActivity {

    NumberKeyboard numberKeyboard;
    TextView myNumber;
    String nr;
    Income newIncome= new Income();
    AppDatabase db;
    static TextView dateChooser;

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


    public boolean isNumeric(String s) {
        return s != null && s.matches("[-+]?\\d*\\.?\\d+");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_income);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("New Income");
        actionbar.setDisplayHomeAsUpEnabled(true);

        Date currentDay = Calendar.getInstance().getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");

        dateChooser=findViewById(R.id.date);
        dateChooser.setText(simpleDateFormat.format(currentDay.getTime()));//set current day

        //listener for keyboard
        numberKeyboard=findViewById(R.id.numberKeyboard);
        myNumber=findViewById(R.id.myNumber);

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class,"Database")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
        dateChooser.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                DialogFragment newFragment = new NewIncome.SelectDateFragment();

                newFragment.show( getSupportFragmentManager(),"DatePicker");
            }
        });


        numberKeyboard.setListener(new NumberKeyboardListener() {
            @Override
            public void onNumberClicked(int number) {
                nr= myNumber.getText().toString();
                myNumber.setText(nr + number);

            }

            @Override
            public void onLeftAuxButtonClicked() {

                nr= myNumber.getText().toString();

                if(nr.length()>0&&isNumeric(nr))
                {
                    myNumber.setText(nr+".");
                }
            }


            @Override
            public void onRightAuxButtonClicked() {
                nr= myNumber.getText().toString();
                if(nr.length()>0) {
                    myNumber.setText(nr.substring(0,nr.length()-1));
                }
            }
        });


    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClickButton(View view)
    {
        String myTextPrice = myNumber.getText().toString();


        if(!myTextPrice.equals(""))
        {
            newIncome.price = parseFloat(myTextPrice);
            try
            {
                SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
                Date parsed = format.parse(dateChooser.getText().toString());
                newIncome.setDate(parsed);
            } catch (Exception Ex) {}

                db.incomeDao().insert(newIncome);
                Toast.makeText(this, "Income added!", Toast.LENGTH_SHORT).show();
                finish();
        }
        else
        {
            Toast.makeText(this, "Sum can't be null", Toast.LENGTH_SHORT).show();
        }
    }

}
