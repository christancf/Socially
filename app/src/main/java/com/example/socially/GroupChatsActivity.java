package com.example.socially;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.socially.adapters.AdapterGroupChatList;
import com.example.socially.models.ModelGroupChats;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class GroupChatsActivity extends AppCompatActivity {

    private RecyclerView groupsRv;

    private FirebaseAuth firebaseAuth;
    ActionBar actionBar;
    private ArrayList<ModelGroupChats> groupChats;
    private AdapterGroupChatList adapterGroupChatList;

    private String firebaseURL = "https://socially-14fd2-default-rtdb.asia-southeast1.firebasedatabase.app";

//    public GroupChatsFragment() {
//        // Required empty public constructor
//    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chats);

        //Setting up a transparent actionbar
        actionBar = getSupportActionBar();
        actionBar.setTitle(Html.fromHtml("<font color='#000000'>Chats</font>"));

        //Setting up a back arrow
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        final Drawable upArrow = getResources().getDrawable(R.drawable.ic_baseline_arrow_back_24);
        upArrow.setColorFilter(Color.parseColor("#000000"), PorterDuff.Mode.SRC_ATOP);
        actionBar.setHomeAsUpIndicator(upArrow);

        groupsRv = findViewById(R.id.groupsRv);
        groupsRv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        firebaseAuth=FirebaseAuth.getInstance();
        loadGroupChats();
    }


//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        View view= inflater.inflate(R.layout.activity_group_chats, container, false);
//
//        groupsRv=view.findViewById(R.id.groupsRv);
//
//        firebaseAuth=FirebaseAuth.getInstance();
//        loadGroupChats();
//        return view;
//    }

    private void loadGroupChats() {
        groupChats=new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance(firebaseURL).getReference("Groups");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                groupChats.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){

                    //if current user's uid exist in participants lis of group then show that group
                    if(ds.child("Participants").child(firebaseAuth.getUid()).exists()){
                        ModelGroupChats model = ds.getValue(ModelGroupChats.class);
                        groupChats.add(model);

                    }
                }
                adapterGroupChatList = new AdapterGroupChatList(getApplicationContext(),groupChats);
                groupsRv.setAdapter(adapterGroupChatList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchGroupChatsList(String query) {
        groupChats=new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance(firebaseURL).getReference("Groups");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                groupChats.clear();
                for(DataSnapshot ds: dataSnapshot.getChildren()){

                    //if current user's uid exist in participants lis of group then show that group
                    if(ds.child("Participants").child(firebaseAuth.getUid()).exists()){

                        //search by group title
                        if(ds.child("groupTitle").toString().toLowerCase().contains(query.toLowerCase())){
                            ModelGroupChats model = ds.getValue(ModelGroupChats.class);
                            groupChats.add(model);
                        }


                    }
                }
                adapterGroupChatList = new AdapterGroupChatList(getApplicationContext(),groupChats);
                groupsRv.setAdapter(adapterGroupChatList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//
//    }

}