package com.jadhavrupesh22.robbychatapp;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsFragment extends Fragment {

    private RecyclerView mFriendsList;

    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mUsersDatabase;

    private FirebaseAuth mAuth;

    private String mCurrent_user_id;

    private View mMainView;
    private DatabaseReference mUserDatabase;



    public FriendsFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_friends, container, false);

        mFriendsList = (RecyclerView) mMainView.findViewById(R.id.friends_list);
        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_id = mAuth.getCurrentUser().getUid();
        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_user_id);
        mFriendsDatabase.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);
        //mDatabase=FirebaseDatabase.getInstance();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mFriendsList.setHasFixedSize(true);
        mFriendsList.setLayoutManager(new LinearLayoutManager(getContext()));

        // Inflate the layout for this fragment
        return mMainView;
    }


    @Override
    public void onStart() {
        super.onStart();

        FirebaseRecyclerOptions<Friends> options =
                new FirebaseRecyclerOptions.Builder<Friends>()
                        .setQuery(mFriendsDatabase,Friends.class)
                        .build();

        FirebaseRecyclerAdapter<Friends,FriendsViewHolder> adapter= new FirebaseRecyclerAdapter<Friends,FriendsViewHolder>(options){
            @NonNull
            @Override
            public FriendsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_single_layout, parent, false);
                FriendsViewHolder friendsViewHolder=new FriendsViewHolder(view);
                return friendsViewHolder;
            }

            @Override
            protected void onBindViewHolder(@NonNull final FriendsViewHolder friendsViewHolder, int i, @NonNull Friends friends) {

                friendsViewHolder.setDate(friends.getDate());
//                friendsViewHolder.mStatus.setText(model.getStatus());
//                Picasso.get().load(model.getThumb_image()).into(holder.mDisplayImage);

                final String user_Id=getRef(i).getKey();

//                friendsViewHolder.mView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        Intent profileIntent=new Intent(UsersActivity.this,ProfileActivity.class);
//                        profileIntent.putExtra("user_Id",user_Id);
//                        startActivity(profileIntent);
//                    }
//                });

//                friendsViewHolder.setDate(friends.getDate());
//
//                final String list_user_id = getRef(i).getKey();
//                mUserDatabase.child(list_user_id).addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                        final String userName = dataSnapshot.child("name").getValue().toString();
//                        String userThumb = dataSnapshot.child("thumb_image").getValue().toString();
//
//                        friendsViewHolder.setDate(userName);
//
//
//
//
//
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                    }
//                });

            }
        };
        mFriendsList.setAdapter(adapter);
        adapter.startListening();

    }

    public static class FriendsViewHolder extends RecyclerView.ViewHolder {

        View mView;

        public FriendsViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

        }

        public void setDate(String date){

            TextView userStatusView = (TextView) mView.findViewById(R.id.user_single_status);
            userStatusView.setText(date);

        }

//        public void setName(String name){
//
//            TextView userNameView = (TextView) mView.findViewById(R.id.user_single_name);
//            userNameView.setText(name);
//
//        }
//
//        public void setUserImage(String thumb_image, Context ctx){
//
//            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.user_single_image);
//            Picasso.get().load(thumb_image).into(userImageView);
//
//        }


    }

}