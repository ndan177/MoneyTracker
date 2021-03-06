package com.example.nohai.moneytracker.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.ToxicBakery.viewpager.transforms.CubeOutTransformer;
import com.example.nohai.moneytracker.DayViewFragment;
import com.example.nohai.moneytracker.ExpenseList;
import com.example.nohai.moneytracker.IncomeList;
import com.example.nohai.moneytracker.Utils.PersonalCubeOutTransformer;
import com.example.nohai.moneytracker.WeekViewFragment;
import com.example.nohai.moneytracker.MonthViewFragment;
import com.example.nohai.moneytracker.YearViewFragment;
import com.example.nohai.moneytracker.R;
import com.mynameismidori.currencypicker.CurrencyPicker;
import com.mynameismidori.currencypicker.CurrencyPickerListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity{
    private DrawerLayout mDrawerLayout;
    TabLayout MyTabs;
    ViewPager MyPage;
    NavigationView navigationView;
    private static Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        MyTabs = findViewById(R.id.MyTabs);
        MyPage = findViewById(R.id.MyPage);

        MyTabs.setupWithViewPager(MyPage);
        SetUpViewPager(MyPage);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        navigationView = findViewById(R.id.nav_view);


        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        int id = menuItem.getItemId();
                        switch (id) {
                            case R.id.expense_chart:
                                Intent intent = new Intent(getApplicationContext(), ExpensesChart.class);
                                intent.putExtra("currency",getCurrency());
                                startActivity(intent);
                                break;
                            case R.id.profit_chart:
                                Intent intent2 = new Intent(getApplicationContext(), ProfitChart.class);
                                intent2.putExtra("currency",getCurrency());
                                startActivity(intent2);
                                break;
                            case R.id.currency:
                                ConnectivityManager cm =
                                  (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

                                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                                boolean isConnected = activeNetwork != null &&
                                        activeNetwork.isConnectedOrConnecting();
                                if(isConnected==false)
                                    Toast.makeText(MainActivity.this, "No internet connection", Toast.LENGTH_SHORT).show();
                                else
                                {
                                    Intent intent3 = new Intent(getApplicationContext(), Currency.class);
                                    intent3.putExtra("currency",getCurrency());
                                    startActivity(intent3);
                                }

                                break;
                            case R.id.expenses:
                                Intent intent4 = new Intent(getApplicationContext(), ExpenseList.class);
                                intent4.putExtra("currency",getCurrency());
                                startActivity(intent4);
                                break;

                            case R.id.incomes:
                                Intent intent5 = new Intent(getApplicationContext(), IncomeList.class);
                                intent5.putExtra("currency",getCurrency());
                                startActivity(intent5);
                                break;


                            case R.id.debts:
                                Intent intent7 = new Intent(getApplicationContext(), Debts.class);
                                intent7.putExtra("currency",getCurrency());
                                startActivity(intent7);
                                break;
                            case R.id.backup:
                                Intent intent8 = new Intent(getApplicationContext(), Backup.class);

                                startActivity(intent8);
                                break;
                        }
                       // menuItem.setChecked(true);
                        // close drawer when item is tapped
                       // Toast.makeText(MainActivity.this, ""+menuItem.p), Toast.LENGTH_SHORT).show();
                        mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        return true;
                    }
                });

        mDrawerLayout.addDrawerListener(
                new DrawerLayout.DrawerListener() {
                    @Override
                    public void onDrawerSlide(View drawerView, float slideOffset) {
                        // Respond when the drawer's position changes
                    }

                    @Override
                    public void onDrawerOpened(View drawerView) {
                        // Respond when the drawer is opened

                    }

                    @Override
                    public void onDrawerClosed(View drawerView) {
                        // Respond when the drawer is closed
                    }

                    @Override
                    public void onDrawerStateChanged(int newState) {
                        // Respond when the drawer motion state changes
                    }
                }
        );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
           final CurrencyPicker picker = CurrencyPicker.newInstance("Select Currency");
            picker.show(getSupportFragmentManager(), "CURRENCY_PICKER");
            picker.setListener(new CurrencyPickerListener() {
                @Override
                public void onSelectCurrency(String name, String code, String dialCode, int flagDrawableResID) {

                    saveCurrency(code);
                    loadViewPager();
                   try{ TimeUnit.MILLISECONDS.sleep(400);}catch (Exception ex){}
                    picker.dismiss();
                }
            });
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void SetUpViewPager (ViewPager viewPager){
        MyViewPageAdapter Adapter = new MyViewPageAdapter(getSupportFragmentManager());

        Adapter.AddFragmentPage(new DayViewFragment(), "Day");
        Adapter.AddFragmentPage(new WeekViewFragment(), "Week");
        Adapter.AddFragmentPage(new MonthViewFragment(), "Month");
        Adapter.AddFragmentPage(new YearViewFragment(), "Year");
        //We Need Fragment class now

        viewPager.setAdapter(Adapter);
        viewPager.setPageTransformer(true, new PersonalCubeOutTransformer());

    }

    public class MyViewPageAdapter extends FragmentPagerAdapter{
        private List<Fragment> MyFragment = new ArrayList<>();
        private List<String> MyPageTittle = new ArrayList<>();

        public MyViewPageAdapter(FragmentManager manager){
            super(manager);
        }

        public void AddFragmentPage(Fragment Frag, String Title){
            MyFragment.add(Frag);
            MyPageTittle.add(Title);
        }

        @Override
        public Fragment getItem(int position) {
            return MyFragment.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return MyPageTittle.get(position);
        }

        @Override
        public int getCount() {
            return 4;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }
    public void openExpense(View view) {
        Intent intent = new Intent(this, NewExpense.class);
        startActivity(intent);
    }
    public void openIncome(View view) {
        Intent intent = new Intent(this, NewIncome.class);
        startActivity(intent);
    }

    public void saveCurrency(String code)
    {
        SharedPreferences sharedPref = getSharedPreferences("CURRENCY",Context.MODE_PRIVATE);
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
    public String getSharedDate()
    {
        SharedPreferences sharedPref = getSharedPreferences("DATE",Context.MODE_PRIVATE);
        String defaultValue = getResources().getString(R.string.saved_date);
        String myDate = sharedPref.getString(getString(R.string.saved_date), defaultValue);
        return myDate;
    }
    public void loadViewPager()
    {
        SetUpViewPager(MyPage);
    }

   public static void setBalanceColorRed(TextView t1, TextView t2, TextView t3)
    {
        int color = mContext.getColor(R.color.red);
        t1.setTextColor(color);
        t2.setTextColor(color);
        t3.setTextColor(color);
    }
    public static void setBalanceColorGreen(TextView t1,TextView t2,TextView t3)
    {
        int color = mContext.getColor(R.color.colorAccent);
        t1.setTextColor(color);
        t2.setTextColor(color);
        t3.setTextColor(color);
    }

}



//TODO: map for exchanges points?