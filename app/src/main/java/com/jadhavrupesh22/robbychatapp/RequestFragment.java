package com.jadhavrupesh22.robbychatapp;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class RequestFragment extends Fragment {

    private View RequestFragmentView;
    private FirebaseUser mCurrent_user;
    private RecyclerView myRequestList;
    private DatabaseReference ChatRequestRef, UsersRef, FriendsId;
    private FirebaseAuth mAuth;
    private String mCurrent_user_id;
    String list_user_id = null;
    String currentDate = "0";
    private DatabaseReference mRootRef;


    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        currentDate = DateFormat.getDateTimeInstance().format(new Date());
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_id = mAuth.getCurrentUser().getUid();
        Log.i("Infos", mCurrent_user_id);
        RequestFragmentView = inflater.inflate(R.layout.fragment_request, container, false);
        UsersRef = FirebaseDatabase.getInstance().getReference().child("Users");
        ChatRequestRef = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();
        myRequestList = (RecyclerView) RequestFragmentView.findViewById(R.id.request_list);
        myRequestList.setLayoutManager(new LinearLayoutManager(getContext()));
        return RequestFragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();

        Query query = FirebaseDatabase.getInstance()
//                .getReference().child("Friend_req").child(mCurrent_user_id).orderByChild("request_type").equalTo("received");
                .getReference().child("Friend_req").child(mCurrent_user_id).orderByChild("request_type").equalTo("received");
        FirebaseRecyclerOptions<Users> options = new FirebaseRecyclerOptions.Builder<Users>()
                .setQuery(query, Users.class)
                .build();


        FirebaseRecyclerAdapter<Users, RequestViewHolder> adapter =
                new FirebaseRecyclerAdapter<Users, RequestViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final RequestViewHolder holder, final int position, @NonNull Users model) {

                        list_user_id = getRef(position).getKey();

                        Log.e("Listss", list_user_id);

                        holder.CancelButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                ChatRequestRef.child(mCurrent_user.getUid()).child(getRef(position).getKey()).removeValue();
                                ChatRequestRef.child(getRef(position).getKey()).child(mCurrent_user.getUid()).removeValue();
                                Toast.makeText(getContext(), "Request is Canceled.", Toast.LENGTH_LONG).show();

                            }
                        });

                        holder.AcceptButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Map friendsMap = new HashMap();
                                friendsMap.put("Friends/" + mCurrent_user.getUid() + "/" + getRef(position).getKey() + "/date", currentDate);
                                friendsMap.put("Friends/" + getRef(position).getKey() + "/" + mCurrent_user.getUid() + "/date", currentDate);


                                friendsMap.put("Friend_req/" + mCurrent_user.getUid() + "/" + getRef(position).getKey(), null);
                                friendsMap.put("Friend_req/" + getRef(position).getKey() + "/" + mCurrent_user.getUid(), null);


                                mRootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                                        Toast.makeText(getContext(), "Friend Added...", Toast.LENGTH_SHORT).show();

                                    }
                                });


                            }
                        });


                        UsersRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                                if (dataSnapshot.hasChild("image")) {
                                    final String requestUserName = dataSnapshot.child("name").getValue().toString();
                                    final String requestUserStatus = dataSnapshot.child("status").getValue().toString();
                                    final String requestProfileImage = dataSnapshot.child("image").getValue().toString();

                                    holder.userName.setText(requestUserName);
//                                    holder.usersStatus.setText(requestUserStatus);
                                    Picasso.get().load(requestProfileImage).into(holder.profileImage);
                                } else {
                                    final String requestUserName = dataSnapshot.child("name").getValue().toString();
                                    final String requestUserStatus = dataSnapshot.child("status").getValue().toString();
                                    holder.userName.setText(requestUserName);
//                                    holder.usersStatus.setText(requestUserStatus);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });


//

                        //---Friend list // request Feature-----//


                        //------------Friend list // request Feature-----------


                    }

                    @NonNull
                    @Override
                    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout, parent, false);
                        RequestFragment.RequestViewHolder requestViewHolder = new RequestFragment.RequestViewHolder(view);
                        return requestViewHolder;
                    }
                };
        myRequestList.setAdapter(adapter);
        adapter.startListening();


    }

    private void Decline() {


    }


    public static class RequestViewHolder extends RecyclerView.ViewHolder {

        View mView;
        TextView userName, usersStatus;
        CircleImageView profileImage;
        Button AcceptButton, CancelButton;
        RelativeLayout Root;


        public RequestViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            userName = itemView.findViewById(R.id.user_display_name);
            usersStatus = itemView.findViewById(R.id.user_display_status);
            profileImage = itemView.findViewById(R.id.user_display_image);
            AcceptButton = itemView.findViewById(R.id.users_display_accept);
            CancelButton = itemView.findViewById(R.id.users_display_decline);
            Root = itemView.findViewById(R.id.Root);

        }

        public void setRequest_type(String date) {

            TextView userStatusView = (TextView) mView.findViewById(R.id.user_single_status);
            userStatusView.setText(date);

        }
    }


}
