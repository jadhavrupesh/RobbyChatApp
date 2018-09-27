package com.jadhavrupesh22.robbychatapp;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Camera;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageActivity;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    FirebaseStorage storage = FirebaseStorage.getInstance();
    DataSnapshot dataSnapshot;



    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;
    private StorageReference mStorageRef;
    private static final int GALLERY_PICK = 1;

    //Android Layout
    private CircleImageView mDisplayImage;
    private TextView mName;
    private TextView mStatus;

    //Store Image in FirebaseDatabase
    private StorageReference mImageStorage;

    //progress dialog
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mDisplayImage = (CircleImageView) findViewById(R.id.settings_image);
        mName = (TextView) findViewById(R.id.settings_name);
        mStatus = (TextView) findViewById(R.id.settings_status);
        mImageStorage = FirebaseStorage.getInstance().getReference();

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);
        mStorageRef = FirebaseStorage.getInstance().getReference();

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //retrive value from database
                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                mName.setText(name);
                mStatus.setText(status);
                StorageReference storageReference = FirebaseStorage.getInstance().getReference();

                Glide.with(SettingsActivity.this).load(storageReference).into(mDisplayImage);
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }





    //Move To Status Activity and set Status
    public void cs(View view) {
        String status_value = mStatus.getText().toString();
        Intent statusIntent = new Intent(SettingsActivity.this, StatusActivity.class);
        statusIntent.putExtra("status_value", status_value);
        startActivity(statusIntent);
    }





    //Change Profile Image
    public void ci(View view) {
        CropImage.activity()
                .setAspectRatio(1,1)
                .setCropShape(CropImageView.CropShape.OVAL)
                .setBorderLineColor(Color.rgb(57, 76, 168))
                .setGuidelinesColor(Color.WHITE)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mProgressDialog=new ProgressDialog(SettingsActivity.this);
                mProgressDialog.setTitle("Uploading Image");
                mProgressDialog.setMessage("Please wite while uploading");
                mProgressDialog.setCanceledOnTouchOutside(false);
                mProgressDialog.show();


                //image Url
               Uri resultUri = result.getUri();
                String current_user_id=mCurrentUser.getUid();
                StorageReference filepath=mImageStorage.child("profile_images").child(current_user_id+".jpg");



                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){
                            Uri url= Uri.parse(task.getResult().getMetadata().getReference().getDownloadUrl().toString());
                            String download_url=url.toString();

                            Log.d("hello","this is download link........................................"+download_url);
                            System.out.println(download_url);
                            mProgressDialog.dismiss();
                        }
                        else {
                            Toast.makeText(SettingsActivity.this,"Error in uploading",Toast.LENGTH_LONG).show();
                        }
                    }
                });
                mDisplayImage.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(SettingsActivity.this,""+error,Toast.LENGTH_LONG).show();
            }
        }

    }



}