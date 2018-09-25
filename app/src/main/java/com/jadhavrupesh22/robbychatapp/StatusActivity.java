package com.jadhavrupesh22.robbychatapp;


import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {
    private android.support.v7.widget.Toolbar mToolbar;
    private TextInputLayout mStatus;
    private DatabaseReference mStatusDatabase;
    private FirebaseUser mCurrentUser;


    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        mProgress = new ProgressDialog(this);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String Current_uid = mCurrentUser.getUid();


        mStatusDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(Current_uid);


        mToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.status_appBars);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mStatus = (TextInputLayout) findViewById(R.id.status_input);

        String status_value = getIntent().getStringExtra("status_value");
        mStatus.getEditText().setText(status_value);


    }

    public void ss(View view) {

        mProgress.setTitle("Saving Changes");
        mProgress.setMessage("Please Wait While Changes");
        mProgress.show();

        String status = mStatus.getEditText().getText().toString();

        mStatusDatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    mProgress.dismiss();
                } else {
                    Toast.makeText(StatusActivity.this, "something wrong", Toast.LENGTH_LONG).show();

                }

            }
        });


    }
}
