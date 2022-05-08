package com.example.socially;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
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
import java.util.Locale;


public class GroupChatsFragment extends Fragment {

    private RecyclerView groupsRv;

    private FirebaseAuth firebaseAuth;

    private ArrayList<ModelGroupChats> groupChats;
    private AdapterGroupChatList adapterGroupChatList;

    public GroupChatsFragment() {
        // Required empty public constructor
    }





    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_group_chats, container, false);

        groupsRv=view.findViewById(R.id.groupsRv);

        firebaseAuth=FirebaseAuth.getInstance();
        loadGroupChats();
        return view;
    }

    private void loadGroupChats() {
        groupChats=new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                groupChats.size();
                for(DataSnapshot ds: dataSnapshot.getChildren()){

                    //if current user's uid exist in participants lis of group then show that group
                    if(ds.child("Participants").child(firebaseAuth.getUid()).exists()){
                        ModelGroupChats model = ds.getValue(ModelGroupChats.class);
                        groupChats.add(model);

                    }
                }
                adapterGroupChatList = new AdapterGroupChatList(getActivity(),groupChats);
                groupsRv.setAdapter(adapterGroupChatList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void searchGroupChatsList(String query) {
        groupChats=new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Groups");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                groupChats.size();
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
                adapterGroupChatList = new AdapterGroupChatList(getActivity(),groupChats);
                groupsRv.setAdapter(adapterGroupChatList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}