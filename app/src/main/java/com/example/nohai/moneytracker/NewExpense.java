package com.example.nohai.moneytracker;

import android.arch.persistence.room.Room;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import android.widget.Toast;
import com.davidmiguel.numberkeyboard.NumberKeyboard;
import com.davidmiguel.numberkeyboard.NumberKeyboardListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static java.lang.Float.parseFloat;


public class NewExpense extends AppCompatActivity {
    NumberKeyboard numberKeyboard;
    EditText myNumber;
    String nr;
    Expense newExpense= new Expense();
    AppDatabase db;

    public boolean isNumeric(String s) {
        return s != null && s.matches("[-+]?\\d*\\.?\\d+");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_expense);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("New Expense");
        actionbar.setDisplayHomeAsUpEnabled(true);



        //listener for keyboard
        numberKeyboard=findViewById(R.id.numberKeyboard);
        myNumber=findViewById(R.id.myNumber);
        String categoryId = getIntent().getStringExtra("id");
        //Toast.makeText(this, "TEST"+categoryId, Toast.LENGTH_SHORT).show();

        Date c = Calendar.getInstance().getTime();
        java.sql.Date sqlDate = new java.sql.Date(c.getTime());

        newExpense.setCategoryId(Integer.parseInt(categoryId));
        newExpense.setDate(sqlDate);

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class,"Database")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();


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
        String myTextPrice=myNumber.getText().toString();


        if(!myTextPrice.equals("")) {
            newExpense.price = parseFloat(myTextPrice);
            db.expenseDao().insert(newExpense);
            Toast.makeText(this, "Expense added!", Toast.LENGTH_SHORT).show();
            finish();
        }
          else
            Toast.makeText(this, "Sum cant be null", Toast.LENGTH_SHORT).show();

    }


}
