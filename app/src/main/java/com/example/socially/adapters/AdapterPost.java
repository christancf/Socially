package com.example.socially.adapters;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socially.CommentActivity;
import com.example.socially.CreatePostActivity;
import com.example.socially.OtherUserProfileActivity;
import com.example.socially.R;
import com.example.socially.models.ModelPost;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class AdapterPost extends RecyclerView.Adapter<AdapterPost.MyHolder> {

    Context context;
    List<ModelPost> postList;
    private String firebaseURL = "https://socially-14fd2-default-rtdb.asia-southeast1.firebasedatabase.app";
    String myUid;

    private DatabaseReference likesRef; //for likes database nodes
    private DatabaseReference postsRef; //reference of posts

    boolean mProcessLike = false;

    public AdapterPost(Context mcontext, List<ModelPost> postList) {
        this.context = mcontext;
        this.postList = postList;
        myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        likesRef = FirebaseDatabase.getInstance(firebaseURL).getReference().child("Likes");
        postsRef = FirebaseDatabase.getInstance(firebaseURL).getReference().child("Posts");
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout row_post.xml
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_post, parent, false);

        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, @SuppressLint("RecyclerView") int position) {
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
        String postLikes = postList.get(position).getPostLikes();
        String postComments = postList.get(position).getPComments();

        //convert timestamp to dd/mm/yyyy hh:mm am/pm
        Calendar calendar = Calendar.getInstance(Locale.getDefault());
        calendar.setTimeInMillis(Long.parseLong(publishTime));
        String pTime = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString();

        //set data
        holder.userNameTV.setText(firstName + " " + lastName);
        holder.timeTV.setText(pTime);
        holder.showPostContentTV.setText(postContent);

        try{
            if(!postLikes.isEmpty()) {
                if (postLikes.equals("1")) holder.likesTV.setText(postLikes + " Like");
                else holder.likesTV.setText(postLikes + " Likes");
            }
        } catch (Exception e) {}

        try{
            if(!postComments.isEmpty()) {
                if(postComments.equals("1")) holder.commentsTV.setText(postComments + " Comment");
                else holder.commentsTV.setText(postComments + " Comments");
            }
        }catch (Exception e){}

        //set likes for each post
        setLikes(holder, postID);

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
            holder.publishPostImageIV.setVisibility(View.VISIBLE);
            try {
                Picasso.get().load(postImage).into(holder.publishPostImageIV);
            }
            catch (Exception e) {

            }
        }

        //handle more, like, comment and share
        holder.moreOptionsIV.setOnClickListener(view -> {
            if(uid.equals(myUid)) {
                showPostOptions(view, true, postID, postImage);
            } else {
                showPostOptions(view, false, postID, postImage);
            }
        });

        holder.likeLayoutLL.setOnClickListener(view -> {
            System.out.println(postList.get(position).getPostLikes());
            String pLikes = postList.get(position).getPostLikes();
            System.out.println(pLikes);

            mProcessLike = true;

            //get id of the post clicked
            String postIDe = postList.get(position).getPostID();
            likesRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if(mProcessLike) {
                        if(snapshot.child(postIDe).hasChild(myUid)) {
                            //already liked, so remove like
                            try{
                                int newLikeCount = Integer.parseInt(pLikes) - 1;
                                postsRef.child(postIDe).child("postLikes").setValue("" +newLikeCount);
                                likesRef.child(postIDe).child(myUid).removeValue();
                                mProcessLike = false;
                            } catch (NumberFormatException e) {
//                                    postsRef.child(postIDe).child("postLikes").setValue("1");
//                                    likesRef.child(postIDe).child(myUid).setValue("Liked");
//                                    mProcessLike = false;
                            }

                        } else {
                            //not liked, like it
                            try{
                                int newLikeCount = Integer.parseInt(pLikes) + 1;
                                postsRef.child(postIDe).child("postLikes").setValue("" +newLikeCount);
                                likesRef.child(postIDe).child(myUid).setValue("Liked");
                                mProcessLike = false;
                            } catch (NumberFormatException e) {
                                postsRef.child(postIDe).child("postLikes").setValue("1");
                                likesRef.child(postIDe).child(myUid).setValue("Liked");
                                mProcessLike = false;
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        });

        holder.commentLayoutLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("postId", postID);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

        holder.shareLayoutLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sharebody = "Check out this post I found on Socially" + "\n\n" + postContent;
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject here");
                shareIntent.putExtra(Intent.EXTRA_TEXT, sharebody + "\nhttps://play.google.com/store/apps/details?id=" + context.getPackageName());
                context.startActivity(Intent.createChooser(shareIntent, "Share Via"));
            }
        });

        holder.profileLayoutLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, OtherUserProfileActivity.class);
                intent.putExtra("uid", uid);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
        });

    }

    //show dialog options
    private void showPostOptions(View view, boolean check, String postID, String postImage) {
        final Dialog dialog = new Dialog(view.getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.user_post_options_layout);

        LinearLayout editPost = dialog.findViewById(R.id.editPostLL);
        LinearLayout deletePost = dialog.findViewById(R.id.deletePostLL);
        LinearLayout hidePost = dialog.findViewById(R.id.hidePostLL);

        if(!check) {
            editPost.setVisibility(view.GONE);
            deletePost.setVisibility(view.GONE);
        }

        editPost.setOnClickListener(view1 -> {
            Intent intent = new Intent(context, CreatePostActivity.class);
            intent.putExtra("key", "editPost");
            intent.putExtra("editPostID", postID);
            context.startActivity(intent);
        });

        deletePost.setOnClickListener(view12 -> beginDelete(postID, postImage));
        hidePost.setOnClickListener(view1 -> Toast.makeText(context, "Hide Post", Toast.LENGTH_SHORT).show());

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.bottomDialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

//    private void showMoreOptions(ImageView moreOptionsIV, String uid, String myUid, String postID, String postImage) {
//        PopupMenu popupMenu = new PopupMenu(context, moreOptionsIV, Gravity.END);
//        //show delete option in only posts of currently signed in user
//        if(uid.equals(myUid)) {
//            popupMenu.getMenu().add(Menu.NONE, 0, 0, "Delete Post");
//        }
//        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem menuItem) {
//                int id = menuItem.getItemId();
//                if(id == 0) {
//                    //delete is clicked
//                    beginDelete(postID, postImage);
//                }
//                return false;
//            }
//        });
//        popupMenu.show();
//    }

    private void beginDelete(String postID, String postImage) {
        if(postImage.equals("noImage")) {
            deleteWithoutImage(postID);
        } else {
            deleteWithImage(postID, postImage);
        }
    }

    private void deleteWithImage(String postID, String postImage) {
        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Deleting Post...");

        StorageReference picRef = FirebaseStorage.getInstance().getReferenceFromUrl(postImage);
        picRef.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Query fquery = FirebaseDatabase.getInstance(firebaseURL).getReference("Posts").orderByChild("postID").equalTo(postID);
                        fquery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for(DataSnapshot ds : snapshot.getChildren()) {
                                    ds.getRef().removeValue();
                                }
                                Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                pd.dismiss();
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        pd.dismiss();
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteWithoutImage(String postID) {

        final ProgressDialog pd = new ProgressDialog(context);
        pd.setMessage("Deleting Post...");

        Query fquery = FirebaseDatabase.getInstance(firebaseURL).getReference("Posts").orderByChild("postID").equalTo(postID);
        fquery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds : snapshot.getChildren()) {
                    ds.getRef().removeValue();
                }
                Toast.makeText(context, "Deleted Successfully", Toast.LENGTH_SHORT).show();
                pd.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setLikes(MyHolder myHolder, String postKey) {
        likesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(postKey).hasChild(myUid)) {
                    Glide.with(context).load(R.drawable.ic_like_filled_orange).into(myHolder.likeIV);
                    myHolder.likeActionTV.setText("Liked");

                } else {
                    Glide.with(context).load(R.drawable.ic_like_outlined).into(myHolder.likeIV);
                    myHolder.likeActionTV.setText("Like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

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
        TextView userNameTV, timeTV, showPostContentTV, likesTV, commentsTV, likeActionTV;
        ImageView moreOptionsIV, publishPostImageIV, likeIV, commentIV, shareIV;
        LinearLayout profileLayoutLL, likeLayoutLL, commentLayoutLL, shareLayoutLL;


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
            profileLayoutLL = itemView.findViewById(R.id.profile_layout);
            likeLayoutLL = itemView.findViewById(R.id.like_layout);
            commentLayoutLL = itemView.findViewById(R.id.comment_layout);
            shareLayoutLL = itemView.findViewById(R.id.share_layout);
            likeActionTV = itemView.findViewById(R.id.tv_like_action);
        }
    }
}
