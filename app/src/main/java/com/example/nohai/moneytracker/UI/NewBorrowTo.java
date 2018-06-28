package com.example.nohai.moneytracker.UI;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.ContactsContract;

import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.provider.ContactsContract.Contacts;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.davidmiguel.numberkeyboard.NumberKeyboard;
import com.davidmiguel.numberkeyboard.NumberKeyboardListener;
import com.example.nohai.moneytracker.AppDatabase;
import com.example.nohai.moneytracker.Database.Debt;
import com.example.nohai.moneytracker.NotificationPublisher;
import com.example.nohai.moneytracker.R;
import com.example.nohai.moneytracker.Utils.DateHelper;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import android.app.TimePickerDialog;

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
    static TextView timeChooser;
    static CheckBox checkBox;
    static TextView phoneEntry;

    static final int MAX_INPUT = 9;
    String nr;

    public static class SelectTimeFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        public void onTimeSet(TimePicker view, int hour, int minute){
            populateSetTime(hour, minute);
        }
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int hourOfDay = 12;
            int minute = 0;
            return new TimePickerDialog(getActivity(), this, hourOfDay,minute,true);
        }
        public void populateSetTime(int hour, int minute) {
            if(minute<10)
                timeChooser.setText(hour+":0"+minute);
            else
                timeChooser.setText(hour+":"+minute);
        }
    }

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
        setContentView(R.layout.activity_borrow);
        setToolbar();

        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class,"Database")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();

        phoneEntry = findViewById(R.id.contact);
        checkBox = findViewById(R.id.checkBox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    timeChooser.setVisibility(View.VISIBLE);
                else
                    timeChooser.setVisibility(View.GONE);
            }
        });

        dateChooser = findViewById(R.id.date);
        timeChooser = findViewById(R.id.time);

        dateChooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                DialogFragment newFragment = new SelectDateFragment();
                newFragment.show(getSupportFragmentManager(),"DatePicker");
            }
        });
        timeChooser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                DialogFragment newFragment = new SelectTimeFragment();
                newFragment.show(getSupportFragmentManager(),"TimePicker");
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
        editOrNew();
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

                        phoneEntry.setText(phone);
                        //phoneEntry.setText(id);
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
        newDebt.notify = checkBox.isChecked();
        newDebt.notifyHour = timeChooser.getText().toString();
        Intent replyIntent = new Intent();
        Date parsedDate ;
        Date parsedDateLimit=null ;
        try
        {
            Date currentDay = Calendar.getInstance().getTime();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
            SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy");
            parsedDate = format.parse(simpleDateFormat.format(currentDay.getTime()).toString());
            newDebt.setDate(parsedDate);
            parsedDateLimit = format.parse(dateChooser.getText().toString());
            newDebt.setDateLimit(parsedDateLimit);//TODO: get this date and add hour

        } catch (Exception Ex) {}

        if(!myTextPrice.equals(""))
        {
            newDebt.price = Double.parseDouble(myTextPrice);
            if(contactId!= -1) {
                if(checkBox.isChecked()&&dateChooser.getText().toString().isEmpty())
                {
                    Toast.makeText(this, "Select a date", Toast.LENGTH_SHORT).show();
                    return;
                }
                newDebt.contactId=contactId;
                replyIntent.putExtra("to",  true);
                replyIntent.putExtra("price",  newDebt.price);
                replyIntent.putExtra("contactId",  newDebt.contactId);
                replyIntent.putExtra("notes",  newDebt.notes);
                replyIntent.putExtra("date",  newDebt.date);
                replyIntent.putExtra("dateLimit",  newDebt.dateLimit);
                if(newDebt.getId()==0)
                    setResult(RESULT_OK, replyIntent);
                else
                    db.debtDao().update(newDebt);
                if(checkBox.isChecked())
                {
                    //get hour and minutes from timeChooser
                    String[] time = timeChooser.getText().toString().split ( ":" );
                    int hour = Integer.parseInt ( time[0].trim() );
                    int min = Integer.parseInt ( time[1].trim() );
                    long delay = parsedDateLimit.getTime();

                    //we must parse time from timeChooser and add it here
                    delay+= TimeUnit.HOURS.toMillis(hour);
                    delay+= TimeUnit.MINUTES.toMillis(min);

                    //get milliseconds from now on to start the notification
                    delay-=System.currentTimeMillis();
                    scheduleNotification(getNotification("Borrow to: "+Debts.getContactName( newDebt.contactId)), (int)(delay));
                }

                finish();
                //RESULT OK
            }
            else{
                Toast.makeText(this, "Please select a contact", Toast.LENGTH_SHORT).show();
                setResult(RESULT_CANCELED, replyIntent);
            }
        }
        else
        {
            Toast.makeText(this, "Sum field can't be empty", Toast.LENGTH_SHORT).show();
        }
    }

    private void scheduleNotification(Notification notification, int delay) {

        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    private Notification getNotification(String content) {
        Intent intent = new Intent(this, Debts.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("Scheduled Notification");
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.ic_attach_money_black_24dp);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);
        return builder.build();
    }

    void editOrNew()
    {
        //for edit debt
        int intentDebtId = getIntent().getIntExtra("borrowId",-1) ;
        if(intentDebtId != -1)
        {
            newDebt = db.debtDao().getDebtById(intentDebtId);
            editBorrow(newDebt);

        }


    }
    void editBorrow(Debt debt)
    {
        myNumber.setText(String.valueOf(debt.price));
        myNotes.setText(debt.notes);
        if(debt.dateLimit!=null) {
            String dateString = DateHelper.dateChooserDateFormatList(debt.dateLimit).toString();
            dateChooser.setText(dateString);
        }
        checkBox.setChecked(debt.notify);
        if(debt.notify) timeChooser.setText(debt.notifyHour);
        contactId = debt.contactId;
        phoneEntry.setText( Debts.getContactName(debt.contactId));
    }

}
