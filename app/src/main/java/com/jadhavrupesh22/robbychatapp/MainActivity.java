package com.jadhavrupesh22.robbychatapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import android.view.Menu;
import android.view.MenuItem;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    //private SectionPagerAdapter mSectionsPagerAdapter;

    //Firebase
    private FirebaseAuth mAuth;

    //Toolbar
    private android.support.v7.widget.Toolbar mToolbar;

    //
    private TabLayout mTablayout;

    private ViewPager mViewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar=(android.support.v7.widget.Toolbar)findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Robby-Chat");

        //Firebase
        mAuth = FirebaseAuth.getInstance();

        //Tabs
        mViewPager=(ViewPager)findViewById(R.id.main_tabPager );

//        mSectionsPagerAdapter=new SectionPagerAdapter(getSupportFragmentManager());
//        mViewPager.setAdapter(mSectionsPagerAdapter);
//        mTablayout=(TabLayout)findViewById(R.id.main_tabs);
//        mTablayout.setupWithViewPager(mViewPager);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            sendToStart();
        }
    }

    private void sendToStart() {
        Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();
    }


}
