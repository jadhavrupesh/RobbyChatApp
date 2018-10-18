package com.jadhavrupesh22.robbychatapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

public class MainActivity extends AppCompatActivity {

    private SectionPagerAdapter mSectionsPagerAdapter;



    //Firebase
    private FirebaseAuth mAuth;
    private DatabaseReference mUserRef;

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
        mUserRef=FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        //Tabs
        mViewPager=(ViewPager)findViewById(R.id.main_tabPager );
        mSectionsPagerAdapter=new SectionPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mTablayout=(TabLayout)findViewById(R.id.main_tabs);
        mTablayout.setupWithViewPager(mViewPager);



    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            sendToStart();
        }else {
            mUserRef.child("online").setValue("true");
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        mUserRef.child("online").setValue(ServerValue.TIMESTAMP);
    }

    private void sendToStart() {
        Intent startIntent = new Intent(MainActivity.this, StartActivity.class);
        startActivity(startIntent);
        finish();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    public void Logout(MenuItem item) {
        FirebaseAuth.getInstance().signOut();
        sendToStart();
        mUserRef.child("online").setValue(ServerValue.TIMESTAMP);

    }

    public void alu(MenuItem item) {
        Intent userIntent=new Intent(MainActivity.this,UsersActivity.class);
        startActivity(userIntent);
    }

    public void as(MenuItem item) {
        Intent settingIntent=new Intent(MainActivity.this,SettingsActivity.class);
        startActivity(settingIntent);
    }


}
