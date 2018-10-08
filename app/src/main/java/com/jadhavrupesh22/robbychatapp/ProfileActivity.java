package com.jadhavrupesh22.robbychatapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {

    private TextView profileId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        String user_Id=getIntent().getStringExtra("user_Id");

        profileId=(TextView)findViewById(R.id.profile_id);

        profileId.setText(user_Id);

    }
}
