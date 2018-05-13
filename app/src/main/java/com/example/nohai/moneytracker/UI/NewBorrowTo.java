package com.example.nohai.moneytracker.UI;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.arch.persistence.room.Room;
import android.content.Intent;

import android.database.Cursor;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;

import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.provider.ContactsContract.Contacts;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.davidmiguel.numberkeyboard.NumberKeyboard;
import com.davidmiguel.numberkeyboard.NumberKeyboardListener;
import com.example.nohai.moneytracker.AppDatabase;
import com.example.nohai.moneytracker.Database.Debt;
import com.example.nohai.moneytracker.R;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class NewBorrowTo extends AppCompatActivity {
    private static final int CONTACT_PICKER_RESULT = 1001;
    private static String DEBUG_TAG = "My App Logging";
    private int contactId= -1;
    NumberKeyboard numberKeyboard;
    TextView myNumber;
    AppDatabase db;
    Debt newDebt= new Debt();
    static TextView dateChooser;
    static EditText myNotes;
    static ImageView dateExpired;
    static final int MAX_INPUT = 9;
    String nr;

    public static String getMonthName(int month) {
        return new DateFormatSymbols().getShortMonths()[month-1];
    }
    public boolean isNumeric(String s) {
        return s != null && s.matches("[-+]?\\d*\\.?\\d+");
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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_borrow_to);
        setToolbar();

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class,"Database")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();

        dateChooser = findViewById(R.id.date);

        dateChooser.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                DialogFragment newFragment = new SelectDateFragment();
                newFragment.show(getSupportFragmentManager(),"DatePicker");
            }
        });
        dateExpired =  findViewById(R.id.imageViewExpired);
        dateExpired.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                DialogFragment newFragment = new SelectDateFragment();
                newFragment.show(getSupportFragmentManager(),"DatePicker");
            }
        });
        myNotes =  findViewById(R.id.notes);

        //listener for keyboard
        numberKeyboard=findViewById(R.id.numberKeyboard);
        myNumber=findViewById(R.id.myNumber);
        numberKeyboard.setListener(new NumberKeyboardListener() {
            @Override
            public void onNumberClicked(int number) {
                nr = myNumber.getText().toString();

                if(nr.indexOf('.')<0)
                {if(nr.length()< MAX_INPUT )  myNumber.setText(nr + number);}
                else {
                    if (nr.length() - nr.indexOf('.') <= 2)
                        if (nr.length() < MAX_INPUT) myNumber.setText(nr + number);
                }
            }

            @Override
            public void onLeftAuxButtonClicked() {

                nr = myNumber.getText().toString();

                if(nr.length()>0 && isNumeric(nr) && nr.indexOf('.')<0)
                {
                    if(nr.length()< MAX_INPUT ) myNumber.setText(nr+".");
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

    public void doLaunchContactPicker(View view) {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                Contacts.CONTENT_URI);
        startActivityForResult(contactPickerIntent, CONTACT_PICKER_RESULT);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CONTACT_PICKER_RESULT:
                    Cursor cursor = null;
                    String phone = "";
                    String id="";
                    try {
                        Uri result = data.getData();
                        Log.v(DEBUG_TAG, "Got a contact result: "
                                + result.toString());

                        // get the contact id from the Uri
                         id = result.getLastPathSegment();
                        contactId= Integer.parseInt(id);

                        // query for everything phone
                        cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?", new String[] { id },
                                null);

                        int phoneIdx = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

                        // let's just get the first phone
                        if (cursor.moveToFirst()) {
                            phone = cursor.getString(phoneIdx);
                            Log.v(DEBUG_TAG, "Got phone: " + phone);
                        } else {
                            Log.w(DEBUG_TAG, "No results");
                        }
                    } catch (Exception e) {
                        Log.e(DEBUG_TAG, "Failed to get phone data", e);
                    } finally {
                        if (cursor != null) {
                            cursor.close();
                        }
                        TextView phoneEntry = findViewById(R.id.category);
                        //phoneEntry.setText(phone);
                        phoneEntry.setText(id);
                        if (phone.length() == 0) {
                            Toast.makeText(this, "No phone found for contact.",
                                    Toast.LENGTH_LONG).show();
                        }

                    }

                    break;
            }

        } else {
            Log.w(DEBUG_TAG, "Warning: activity result not ok");
        }
    }




    void setToolbar()
    {
        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("Borrow to");
        actionbar.setDisplayHomeAsUpEnabled(true);
    }
    public void save(View view)
    {
        String myTextPrice = myNumber.getText().toString();
        newDebt.notes  = myNotes.getText().toString();
        Intent replyIntent = new Intent();
        try
        {
            Date currentDay = Calendar.getInstance().getTime();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
            SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
            Date parsed = format.parse(simpleDateFormat.format(currentDay.getTime()).toString());
            newDebt.setDate(parsed);
            Date parsed2 = format.parse(dateChooser.getText().toString());
            newDebt.setDateLimit(parsed2);
        } catch (Exception Ex) {}

        if(!myTextPrice.equals(""))
        {
            newDebt.price = Double.parseDouble(myTextPrice);
            if(contactId!= -1) {
                newDebt.contactId=contactId;
                replyIntent.putExtra("price",  newDebt.price);
                replyIntent.putExtra("contactId",  newDebt.contactId);
                replyIntent.putExtra("notes",  newDebt.notes);
                replyIntent.putExtra("date",  newDebt.date);
                replyIntent.putExtra("dateLimit",  newDebt.dateLimit);
                setResult(RESULT_OK, replyIntent);

                finish();
            }
            else{
                Toast.makeText(this, "Please select a contact", Toast.LENGTH_SHORT).show();
                setResult(RESULT_CANCELED, replyIntent);
            }

        }
        else
        {
            Toast.makeText(this, "Sum can't be null", Toast.LENGTH_SHORT).show();
        }


    }

}
