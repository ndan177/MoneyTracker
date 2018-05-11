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
    AppDatabase db;
    Debt newDebt= new Debt();
    static TextView dateChooser;
    static EditText myNotes;
    static ImageView dateExpired;

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

        newDebt.notes  = myNotes.getText().toString();
        Intent replyIntent = new Intent();
        try
        {
            Date currentDay = Calendar.getInstance().getTime();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
            SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
            Date parsed = format.parse(simpleDateFormat.format(currentDay.getTime()).toString());
            newDebt.setDate(parsed);
        } catch (Exception Ex) {}
        //Toast.makeText(this, ""+contactId, Toast.LENGTH_SHORT).show();
        if(contactId!= -1) {
            newDebt.contactId=contactId;
            //db.debtDao().insert(newDebt);

            replyIntent.putExtra("contactId",  newDebt.contactId);
            replyIntent.putExtra("notes",  newDebt.notes);
            replyIntent.putExtra("date",  newDebt.date);
            setResult(RESULT_OK, replyIntent);
            Toast.makeText(this, "Debt added!", Toast.LENGTH_SHORT).show();
            finish();
        }
        else{
            Toast.makeText(this, "Please select a contact", Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED, replyIntent);
        }


    }

}
