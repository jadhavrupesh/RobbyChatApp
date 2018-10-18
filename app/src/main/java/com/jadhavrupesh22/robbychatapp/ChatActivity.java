package com.jadhavrupesh22.robbychatapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

public class ChatActivity extends AppCompatActivity {
    private String mChatUser;
    private Toolbar mChatToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mChatToolbar=(Toolbar)findViewById(R.id.chat_app_bar);
        setSupportActionBar(mChatToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mChatUser=getIntent().getStringExtra("user_id");
        getSupportActionBar().setTitle(mChatUser);


    }
}
