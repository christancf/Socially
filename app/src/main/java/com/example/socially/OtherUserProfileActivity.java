package com.example.socially;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socially.activities.ChatActivity;
import com.example.socially.adapters.AdapterPost;
import com.example.socially.models.ModelPost;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OtherUserProfileActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference db;
    private String firebaseURL = "https://socially-14fd2-default-rtdb.asia-southeast1.firebasedatabase.app";

    String userName, userEmail, userProfilePicture, userCoverPicture;

    ImageView profileImageIV, coverImageIV;
    TextView profileIdTV;

    TextView output;
    ImageView chatIcon;
    String UID;

    RecyclerView userPostRecyclerView;
    List<ModelPost> postList;
    AdapterPost adapterPost;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other_user_profile);

        firebaseAuth = FirebaseAuth.getInstance();

        //get uid of clicked user to retrieve his posts
        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");

        //output = findViewById(R.id.output);
        chatIcon = findViewById(R.id.chat_icon);
        profileImageIV = findViewById(R.id.profileImageIV);
        coverImageIV = findViewById(R.id.coverImageIV);
        profileIdTV = findViewById(R.id.profileIdTV);

        //recycler view
        userPostRecyclerView = findViewById(R.id.recycler_view_user_post);
        LinearLayoutManager layoutManager= new LinearLayoutManager(this);

        //show latest posts first
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        //set layout to recycler view
        userPostRecyclerView.setLayoutManager(layoutManager);

        //init post list
        postList = new ArrayList<>();

        checkUserStatus();
        loadHisPosts();

//        UID = getIntent().getStringExtra("UID");
//        output.setText(UID);


        chatIcon.setOnClickListener(view -> {
            Toast.makeText(getApplicationContext(), "Opening Chat...", Toast.LENGTH_SHORT).show();
            //Chat Function
            Intent intent2 = new Intent(this, ChatActivity.class);
            intent2.putExtra("hisUid", UID);
            this.startActivity(intent2);

        });
    }

    private void loadHisPosts() {
        //linear layout for recycler view
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //show latest post first
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        //set this layout to recycler view
        userPostRecyclerView.setLayoutManager(layoutManager);

        //init post list
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Path");
        //query to load posts
        Query query = ref.orderByChild("uid").equalTo(uid);
        //get all data from this ref
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for(DataSnapshot ds : snapshot.getChildren()) {
                    ModelPost myPosts = ds.getValue(ModelPost.class);

                    //add to list
                    postList.add(myPosts);

                    //adapter
                    adapterPost = new AdapterPost(getApplicationContext(), postList);
                    //set this adapter to recycler view
                    userPostRecyclerView.setAdapter(adapterPost);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OtherUserProfileActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchHisPosts(final String searchQuery) {
        //linear layout for recycler view
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        //show latest post first
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        //set this layout to recycler view
        userPostRecyclerView.setLayoutManager(layoutManager);

        //init post list
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Path");
        //query to load posts
        Query query = ref.orderByChild("uid").equalTo(uid);
        //get all data from this ref
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for(DataSnapshot ds : snapshot.getChildren()) {
                    ModelPost myPosts = ds.getValue(ModelPost.class);

                    //add to list
                    if(myPosts.getPostContent().toLowerCase().contains(searchQuery.toLowerCase())
                            || myPosts.getFirstName().toLowerCase().contains(searchQuery.toLowerCase())
                            || myPosts.getLastName().toLowerCase().contains(searchQuery.toLowerCase())) {

                        postList.add(myPosts);
                    }

                    //adapter
                    adapterPost = new AdapterPost(getApplicationContext(), postList);
                    //set this adapter to recycler view
                    userPostRecyclerView.setAdapter(adapterPost);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(OtherUserProfileActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkUserStatus(){
        FirebaseUser user = firebaseAuth.getCurrentUser();
        //Log.d(TAG, "checkUserStatus: function called. User = " + user);
        if(user != null){
//            userEmail = user.getEmail();
//            setupUserProfile();
        }else{
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.post_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search post...");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(!TextUtils.isEmpty(query)) {
                    searchHisPosts(query);
                } else {
                    loadHisPosts();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(!TextUtils.isEmpty(newText)) {
                    searchHisPosts(newText);
                } else {
                    loadHisPosts();
                }
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        //if(id == R.id.action_lo)
        return super.onOptionsItemSelected(item);
    }
}