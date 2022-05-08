package com.example.socially.adapters;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socially.R;
import com.example.socially.models.ModelPost;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterPost extends RecyclerView.Adapter<AdapterPost.MyHolder> {

    //ValueEventListener c;
    Context context;
    List<ModelPost> postList;

    public AdapterPost(Context mcontext, List<ModelPost> postList) {
        this.context = mcontext;
        this.postList = postList;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout row_post.xml
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_post, parent, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        //get data
        String uid = postList.get(position).getUid();
        String firstName = postList.get(position).getFirstName();
        String lastName = postList.get(position).getLastName();
        String email = postList.get(position).getEmail();
        String profileImage = postList.get(position).getProfileImage();
        String postID = postList.get(position).getPostID();
        String postContent = postList.get(position).getPostContent();
        String postImage = postList.get(position).getPostImage();
        String publishTime = postList.get(position).getPublishTime();

        //convert timestamp to dd/mm/yyyy hh:mm am/pm
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(publishTime));
        String pTime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();

        //set data
        holder.userNameTV.setText(firstName + " " + lastName);
        holder.timeTV.setText(pTime);
        holder.showPostContentTV.setText(postContent);

        //set user profile image
        try {
            //Picasso.get().load(profileImage).into(holder.postProfilePictureCIV);
            Glide.with(context).asBitmap().load(profileImage).into(holder.postProfilePictureCIV);
        }
        catch (Exception e) {

        }

        //set post image
        //if there is no post image, then hide image
        if(postImage.equals("noImage")) {
            holder.publishPostImageIV.setVisibility(View.GONE);
        } else {
            try {
                Picasso.get().load(postImage).into(holder.publishPostImageIV);
            }
            catch (Exception e) {

            }
        }

        //handle more, like, comment and share
        holder.moreOptionsIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "More Options", Toast.LENGTH_SHORT).show();
            }
        });

        holder.likeIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Like", Toast.LENGTH_SHORT).show();
            }
        });

        holder.commentIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Comment", Toast.LENGTH_SHORT).show();
            }
        });

        holder.shareIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Share", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public int getItemCount() {
        return postList.size();
    }

    //view holder class
    class MyHolder extends RecyclerView.ViewHolder {

        //views from row_post.xml
        CircleImageView postProfilePictureCIV;
        TextView userNameTV, timeTV, showPostContentTV, likesTV, commentsTV;
        ImageView moreOptionsIV, publishPostImageIV, likeIV, commentIV, shareIV;


        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //init views
            postProfilePictureCIV = itemView.findViewById(R.id.civ_post_profile_pic);
            userNameTV = itemView.findViewById(R.id.tv_user_name);
            timeTV = itemView.findViewById(R.id.tv_time);
            showPostContentTV = itemView.findViewById(R.id.tv_show_post_content);
            likesTV = itemView.findViewById(R.id.tv_likes);
            commentsTV = itemView.findViewById(R.id.tv_comments);
            moreOptionsIV = itemView.findViewById(R.id.iv_more_options);
            publishPostImageIV = itemView.findViewById(R.id.iv_publish_post_image);
            likeIV = itemView.findViewById(R.id.iv_like);
            commentIV = itemView.findViewById(R.id.iv_comment);
            shareIV = itemView.findViewById(R.id.iv_share);
        }
    }
}
