package com.jadhavrupesh22.robbychatapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
    }

    public void srb(View view) {
        Intent reg_Intent=new Intent(StartActivity.this,RegisterActivity.class);
        startActivity(reg_Intent);

    }

    public void login(View view) {

        Intent loginIntent=new Intent(StartActivity.this,LoginActivity.class);
        startActivity(loginIntent);

    }



}
