package com.jadhavrupesh22.robbychatapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;

public class ProfileActivity extends AppCompatActivity {

    private ImageView mProfileImage;
    private Button mProfileSendReqBtn;
    private TextView mProfileName, mProfileStatus,mProfileFriendCount;
    private DatabaseReference mUserDatabase;
    private DatabaseReference mFriendDatabase;
    private DatabaseReference mFriendReqDatabase;
    //progress dialog
    private ProgressDialog mProgressDialog;
    private String mCurrent_state;
    private FirebaseUser mCurrent_user;
    private String user_Id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        user_Id = getIntent().getStringExtra("user_Id");
        mProgressDialog =new ProgressDialog(this);
        mProgressDialog.setTitle("Loading User Data");
        mProgressDialog.setMessage("Please wait while loading user Data");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();

        mFriendReqDatabase=FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mFriendDatabase=FirebaseDatabase.getInstance().getReference().child("Friends");
        mUserDatabase=FirebaseDatabase.getInstance().getReference().child("Users").child(user_Id);
        mCurrent_user=FirebaseAuth.getInstance().getCurrentUser();



        mCurrent_state="not_friends";
        mProfileImage=(ImageView)findViewById(R.id.profile_image);
        mProfileSendReqBtn=(Button)findViewById(R.id.profile_send_req_btn);
        mProfileName=(TextView)findViewById(R.id.profile_displayName);
        mProfileStatus=(TextView)findViewById(R.id.profile_status);
        mProfileFriendCount=(TextView)findViewById(R.id.profile_totalFriends);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String displayName=dataSnapshot.child("name").getValue().toString();
                String status=dataSnapshot.child("status").getValue().toString();
                String image=dataSnapshot.child("image").getValue().toString();
                mProfileName.setText(displayName);
                mProfileStatus.setText(status);
                Picasso.get().load(image).placeholder(R.drawable.pp).into(mProfileImage);


                //---Friend request list-----//
                mFriendReqDatabase.child(mCurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.hasChild(user_Id)){
                            String req_type=dataSnapshot.child(user_Id).child("request_type").getValue().toString();
                            if (req_type.equals("received")){
                                mCurrent_state="req_received";
                                mProfileSendReqBtn.setText("Accept Friend Request.");
                            }
                            else if (req_type.equals("sent")){
                                mCurrent_state="req_sent";
                                mProfileSendReqBtn.setText("Cancle Friend Requenst");
                            }
                        }
                        mProgressDialog.dismiss();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mProfileSendReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProfileSendReqBtn.setEnabled(false);


                //-------Not Friend State----//

                if (mCurrent_state.equals("not_friends")){
                    mFriendReqDatabase.child(mCurrent_user.getUid()).child(user_Id).child("request_type")
                            .setValue("send").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                mFriendReqDatabase.child(user_Id).child(mCurrent_user.getUid()).child("request_type")
                                        .setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        mCurrent_state="req_sent";
                                        mProfileSendReqBtn.setText("Cancle Friend Requenst");

                                        Toast.makeText(ProfileActivity.this,"Sending Request Successfully Done.",Toast.LENGTH_LONG).show();
                                    }
                                });
                            }else{
                                Toast.makeText(ProfileActivity.this,"Faild to sending Request.",Toast.LENGTH_LONG).show();
                            }
                            mProfileSendReqBtn.setEnabled(true);
                        }
                    });
                }

                //-------cancle Friend request----//

                if (mCurrent_state.equals("req_sent")){
                    mFriendReqDatabase.child(mCurrent_user.getUid()).child(user_Id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendReqDatabase.child(user_Id).child(mCurrent_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mProfileSendReqBtn.setEnabled(true);
                                    mCurrent_state="not_friends";
                                    mProfileSendReqBtn.setText("Send Friend Requenst");
                                }
                            });

                            Toast.makeText(ProfileActivity.this,"Request is Canceled.",Toast.LENGTH_LONG).show();
                        }
                    });
                }


                //------request receive state---------//


                if (mCurrent_state.equals("req_received")){
                    final String currentDate=DateFormat.getDateInstance().format(new Date());
                    mFriendDatabase.child(mCurrent_user.getUid()).child(user_Id).setValue(currentDate)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    mFriendDatabase.child(user_Id).child(mCurrent_user.getUid()).setValue(currentDate)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {

                                                    mFriendReqDatabase.child(mCurrent_user.getUid()).child(user_Id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            mFriendReqDatabase.child(user_Id).child(mCurrent_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    mProfileSendReqBtn.setEnabled(true);
                                                                    mCurrent_state="Friends";
                                                                    mProfileSendReqBtn.setText("UnFriend this person");
                                                                }
                                                            });

                                                            Toast.makeText(ProfileActivity.this,"Request is Canceled.",Toast.LENGTH_LONG).show();
                                                        }
                                                    });


                                                }
                                            });
                                }
                            });
                }
            }
        });
    }

}
