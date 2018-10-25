package com.jadhavrupesh22.robbychatapp;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
public class RequestFragment extends Fragment {

    private View RequestFragmentView;
    private RecyclerView myRequestList;
    private DatabaseReference ChatRequestRef,UsersRef,FriendsId;
    private FirebaseAuth mAuth;
    private String mCurrent_user_id;


    public RequestFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        RequestFragmentView=inflater.inflate(R.layout.fragment_request, container, false);
        UsersRef=FirebaseDatabase.getInstance().getReference().child("Users");
        ChatRequestRef=FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mAuth = FirebaseAuth.getInstance();
        mCurrent_user_id=mAuth.getCurrentUser().getUid();

        myRequestList=(RecyclerView)RequestFragmentView.findViewById(R.id.request_list);
        myRequestList.setLayoutManager(new LinearLayoutManager(getContext()));

        return RequestFragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();


        FirebaseRecyclerOptions<Users>options=new FirebaseRecyclerOptions.Builder<Users>()
                .setQuery(ChatRequestRef,Users.class)
                .build();



        FirebaseRecyclerAdapter<Users,RequestViewHolder>adapter=
                new FirebaseRecyclerAdapter<Users, RequestViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull final RequestViewHolder holder, int position, @NonNull Users model) {
                        final String list_user_id = getRef(position).getKey();

                        DatabaseReference getTypeRef=getRef(position).child("received").getRef();
                        getTypeRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot.exists()){

                                    String type=dataSnapshot.getValue().toString();

                                    if (type.equals("received")){



                                        UsersRef.child(list_user_id).addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot.hasChild("image")){
                                                    final String requestUserName=dataSnapshot.child("name").getValue().toString();
                                                    final String requestUserStatus=dataSnapshot.child("status").getValue().toString();
                                                    final String requestProfileImage=dataSnapshot.child("image").getValue().toString();

                                                    holder.userName.setText(requestUserName);
                                                    holder.usersStatus.setText(requestUserStatus);
                                                    Picasso.get().load(requestProfileImage).into(holder.profileImage);
                                                }
                                                else {
                                                    final String requestUserName=dataSnapshot.child("name").getValue().toString();
                                                    final String requestUserStatus=dataSnapshot.child("status").getValue().toString();
                                                    holder.userName.setText(requestUserName);
                                                    holder.usersStatus.setText(requestUserStatus);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                    }
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }

                    @NonNull
                    @Override
                    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_display_layout, parent, false);
                        RequestFragment.RequestViewHolder requestViewHolder=new  RequestFragment.RequestViewHolder(view);
                        return requestViewHolder;
                    }
                };
        myRequestList.setAdapter(adapter);
        adapter.startListening();


    }


    public static class RequestViewHolder extends RecyclerView.ViewHolder
    {

        View mView;
        TextView userName,usersStatus;
        CircleImageView profileImage;
        Button AcceptButton,CancelButton;

        public RequestViewHolder(View itemView) {
            super(itemView);

            mView = itemView;

            userName=itemView.findViewById(R.id.user_display_name);
            usersStatus=itemView.findViewById(R.id.user_display_status);
            profileImage=itemView.findViewById(R.id.user_display_image);
            AcceptButton=itemView.findViewById(R.id.users_display_accept);
            CancelButton=itemView.findViewById(R.id.users_display_decline);

        }

        public void setRequest_type(String date){

            TextView userStatusView = (TextView) mView.findViewById(R.id.user_single_status);
            userStatusView.setText(date);

        }
    }
}
