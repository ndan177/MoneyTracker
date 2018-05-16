package com.example.nohai.moneytracker.UI;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.nohai.moneytracker.DebtsHistoryFragment;
import com.example.nohai.moneytracker.DebtsToPayFragment;
import com.example.nohai.moneytracker.DebtsToReceiveFragment;
import com.example.nohai.moneytracker.R;

import java.util.ArrayList;
import java.util.List;


public class Debts extends AppCompatActivity {
    TabLayout MyTabs;
    ViewPager MyPage;
    BottomNavigationView mNavigationView;
    private static final int READ_CONTACTS_PERMISSIONS_REQUEST = 1;
    public static final int NEW_BORROW_FROM_ACTIVITY_REQUEST_CODE = 1;
    public static final int NEW_BORROW_TO_ACTIVITY_REQUEST_CODE = 2;
    public static String currency;
    MyViewPageAdapter Adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debts);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("Debts");
        actionbar.setDisplayHomeAsUpEnabled(true);



          if(getCurrency()==null)
          {
              currency = getIntent().getStringExtra("currency");
              saveCurrency(currency);
          }
          else
          {
              currency=getCurrency();
          }


        MyTabs = findViewById(R.id.MyTabs);
        MyPage = findViewById(R.id.MyPage);

        MyTabs.setupWithViewPager(MyPage);
        SetUpViewPager(MyPage);
        mNavigationView = findViewById(R.id.bottom_navigation);

        mNavigationView.setItemIconTintList(null);
        mNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.borrow_to:
                                openBorrowTo();
                                break;
                            case R.id.borrow_from:
                                openBorrowFrom();
                                break;
                        }
                        return true;
                    }
                });
        getPermissionToReadUserContacts();

    }

    public void SetUpViewPager(ViewPager viewPager) {
         Adapter = new MyViewPageAdapter(getSupportFragmentManager());

        Adapter.AddFragmentPage(new DebtsToPayFragment(), "To Pay");
        Adapter.AddFragmentPage(new DebtsToReceiveFragment(), "To Receive");
        Adapter.AddFragmentPage(new DebtsHistoryFragment(), "History");
        //We Need Fragment class now

        viewPager.setAdapter(Adapter);

    }
    public Fragment getFragment(int pos) {
        return Adapter.getItem(pos);
    }



    public static class MyViewPageAdapter extends FragmentPagerAdapter {
        private List<Fragment> MyFragment = new ArrayList<>();
        private List<String> MyPageTittle = new ArrayList<>();

        public MyViewPageAdapter(FragmentManager manager) {
            super(manager);
        }

        public void AddFragmentPage(Fragment Frag, String Title) {
            MyFragment.add(Frag);
            MyPageTittle.add(Title);
        }

        @Override
        public  Fragment getItem(int position) {
            return MyFragment.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return MyPageTittle.get(position);
        }

        @Override
        public int getCount() {
            return 3;
        }
    }



    void openBorrowFrom() {

        Intent intent = new Intent(this, NewBorrowFrom.class);
        startActivityForResult(intent, NEW_BORROW_FROM_ACTIVITY_REQUEST_CODE);
    }

    void openBorrowTo() {
        Intent intent = new Intent(this, NewBorrowTo.class);
        startActivityForResult(intent, NEW_BORROW_TO_ACTIVITY_REQUEST_CODE);
    }



    // user's contacts
    public void getPermissionToReadUserContacts() {
        // 1) Use the support library version ContextCompat.checkSelfPermission(...) to avoid
        // checking the build version since Context.checkSelfPermission(...) is only available
        // in Marshmallow
        // 2) Always check for permission (even if permission has already been granted)
        // since the user can revoke permissions at any time through Settings
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED) {

            // The permission is NOT already granted.
            // Check if the user has been asked about this permission already and denied
            // it. If so, we want to give more explanation about why the permission is needed.
            if (shouldShowRequestPermissionRationale(
                    Manifest.permission.READ_CONTACTS)) {
                // Show our own UI to explain to the user why we need to read the contacts
                // before actually requesting the permission and showing the default UI
            }

            // Fire off an async request to actually get the permission
            // This will show the standard permission request dialog UI
            requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                    READ_CONTACTS_PERMISSIONS_REQUEST);
        }
    }

    // Callback with the request from calling requestPermissions(...)
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        // Make sure it's our original READ_CONTACTS request
        if (requestCode == READ_CONTACTS_PERMISSIONS_REQUEST) {
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Read Contacts permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Read Contacts permission denied", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NEW_BORROW_FROM_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Fragment fragment = getSupportFragmentManager().getFragments().get(0);
            fragment.onActivityResult(requestCode, resultCode, data);
        }
        if (requestCode == NEW_BORROW_TO_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Fragment fragment = getSupportFragmentManager().getFragments().get(1);
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
    public void saveCurrency(String code)
    {
        SharedPreferences sharedPref = getSharedPreferences("CURRENCY", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("",code);
        editor.commit();
    }

    public String getCurrency()
    {
        SharedPreferences sharedPref = this.getSharedPreferences("CURRENCY",Context.MODE_PRIVATE);
        String defaultValue = getResources().getString(R.string.saved_currency);
        String myCurrency = sharedPref.getString(getString(R.string.saved_currency), defaultValue);
        //if (myCurrency.equals("")){saveCurrency("EUR");return "EUR";}
        return myCurrency;
    }
}
