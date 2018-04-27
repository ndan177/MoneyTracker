package com.example.nohai.moneytracker.UI;



import android.os.Bundle;

import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import com.example.nohai.moneytracker.DebtsToPayFragment;
import com.example.nohai.moneytracker.DebtsToReceiveFragment;
import com.example.nohai.moneytracker.R;
import java.util.ArrayList;
import java.util.List;


public class Debts extends AppCompatActivity {
    TabLayout MyTabs;
    ViewPager MyPage;
    BottomNavigationView mNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debts);

        android.support.v7.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("Debts");
        actionbar.setDisplayHomeAsUpEnabled(true);

        MyTabs = findViewById(R.id.MyTabs);
        MyPage = findViewById(R.id.MyPage);

        MyTabs.setupWithViewPager(MyPage);
        SetUpViewPager(MyPage);
        mNavigationView=findViewById(R.id.bottom_navigation);

        mNavigationView.setItemIconTintList(null);


    }

    public void SetUpViewPager(ViewPager viewPager) {
        MyViewPageAdapter Adapter = new MyViewPageAdapter(getSupportFragmentManager());

        Adapter.AddFragmentPage(new DebtsToPayFragment(), "To Pay");
        Adapter.AddFragmentPage(new DebtsToReceiveFragment(), "To Receive");
        Adapter.AddFragmentPage(new DebtsToReceiveFragment(), "History");

        //We Need Fragment class now

        viewPager.setAdapter(Adapter);

    }

    public class MyViewPageAdapter extends FragmentPagerAdapter {
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
        public Fragment getItem(int position) {
            return MyFragment.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return MyPageTittle.get(position);
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

}
