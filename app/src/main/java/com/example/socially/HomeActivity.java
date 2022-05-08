package com.example.socially;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socially.activities.ChatlistActivity;
import com.example.socially.adapters.AdapterPost;
import com.example.socially.models.ModelPost;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";

    ImageView HomeIV, UsersIV, AddPostIV, ChatIV, ProfileIV;
    FirebaseAuth mAuth;
    ActionBar actionBar;

    RecyclerView recyclerView;
    List<ModelPost> postList;
    AdapterPost adapterPost;

    private String firebaseURL = "https://socially-14fd2-default-rtdb.asia-southeast1.firebasedatabase.app";
    String userProfilePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mAuth = FirebaseAuth.getInstance();

        //Setting up a transparent actionbar
        actionBar = getSupportActionBar();
        actionBar.setTitle(Html.fromHtml("<font color='#000000'>Posts</font>"));
        //actionBar.setBackgroundDrawable(new ColorDrawable(Color.WHITE));

        HomeIV = findViewById(R.id.home);
        UsersIV = findViewById(R.id.users);
        AddPostIV = findViewById(R.id.addPost);
        ChatIV = findViewById(R.id.chat);
        ProfileIV = findViewById(R.id.profile);

        setupUserProfileImage();
        //recycler view
        recyclerView = findViewById(R.id.post_recycler_view);
        LinearLayoutManager layoutManager= new LinearLayoutManager(this);

        //show latest posts first
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

        //set layout to recycler view
        recyclerView.setLayoutManager(layoutManager);

        //init post list
        postList = new ArrayList<>();
        loadPosts();

//        Drawable res = getResources().getDrawable(R.drawable.self_cover);
//        ProfileIV.setImageDrawable(res);

        //create an intent, start activity and work on your function
        HomeIV.setOnClickListener(view -> {
            Toast.makeText(getApplicationContext(), "Home", Toast.LENGTH_SHORT).show();
        });
        UsersIV.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), UsersActivity.class)));

        AddPostIV.setOnClickListener(view -> {
            //Toast.makeText(getApplicationContext(), "Add Post", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), CreatePostActivity.class));
        });

        ChatIV.setOnClickListener(view -> {
            Toast.makeText(getApplicationContext(), "Opening Chat List", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(getApplicationContext(), ChatlistActivity.class));
        });
        ProfileIV.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        });
    }

    private void loadPosts() {
        //path of all posts
        DatabaseReference ref = FirebaseDatabase.getInstance(firebaseURL).getReference("Posts");
        //get all data from this ref
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for(DataSnapshot ds : snapshot.getChildren()) {
                    ModelPost modelPost = ds.getValue(ModelPost.class);

                    postList.add(modelPost);

                    //adapter
                    adapterPost = new AdapterPost(getApplicationContext(), postList);
                    //set adapter to recycler view
                    recyclerView.setAdapter(adapterPost);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //in case of error
                Toast.makeText(HomeActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void searchPosts(String searchQuery) {

        //path of all posts
        DatabaseReference ref = FirebaseDatabase.getInstance(firebaseURL).getReference("Posts");
        //get all data from this ref
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for(DataSnapshot ds : snapshot.getChildren()) {
                    ModelPost modelPost = ds.getValue(ModelPost.class);

                    if(modelPost.getPostContent().toLowerCase().contains(searchQuery.toLowerCase())
                        || modelPost.getFirstName().toLowerCase().contains(searchQuery.toLowerCase())
                        || modelPost.getLastName().toLowerCase().contains(searchQuery.toLowerCase())) {
                        postList.add(modelPost);
                    }

                    //adapter
                    adapterPost = new AdapterPost(getApplicationContext(), postList);
                    //set adapter to recycler view
                    recyclerView.setAdapter(adapterPost);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //in case of error
                Toast.makeText(HomeActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.post_menu,menu);

        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Search post...");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if(!TextUtils.isEmpty(query)) {
                    searchPosts(query);
                } else {
                    loadPosts();
                }
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if(!TextUtils.isEmpty(newText)) {
                    searchPosts(newText);
                } else {
                    loadPosts();
                }
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void setupUserProfileImage() {
        DatabaseReference db = FirebaseDatabase.getInstance(firebaseURL).getReference("Users");
        Query query = db.orderByChild("uid").equalTo(mAuth.getCurrentUser().getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot ds: snapshot.getChildren()) {
                    userProfilePicture = "" + ds.child("profileImage").getValue();
                }
                try {
                    Picasso.get().load(userProfilePicture).into(ProfileIV);
                }catch (Exception e){

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}