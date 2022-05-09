package com.example.socially.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socially.ModelUser;
import com.example.socially.R;
import com.example.socially.models.ModelComment;
import com.squareup.picasso.Picasso;

import java.sql.Date;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{

    private Context context;
    private List<ModelComment> commentsList;

    public CommentAdapter(Context context, List<ModelComment> commentsList) {
        this.context = context;
        this.commentsList = commentsList;
    }

    @NonNull
    @Override
    public CommentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_comment, parent, false);
        CommentAdapter.ViewHolder holder = new CommentAdapter.ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String uid = commentsList.get(position).getUid();
        String fname = commentsList.get(position).getuFName();
        String lname = commentsList.get(position).getuLName();
        String comment = commentsList.get(position).getComment();
        String cid = commentsList.get(position).getCid();
        String image = commentsList.get(position).getuDp();
        String timestamp = commentsList.get(position).getTimestamp();

        holder.usernameTV.setText(fname + " " + lname);

        try{
            Picasso.get().load(image).into(holder.userImageCIV);
        }catch (Exception e){}

        holder.commentTV.setText(comment);


    }

    @Override
    public int getItemCount() {
        return commentsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView userImageCIV;
        TextView usernameTV, commentTV;
        RelativeLayout parentLayoutRL;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userImageCIV = itemView.findViewById(R.id.row_comment_userImageIV);
            usernameTV = itemView.findViewById(R.id.row_comment_usernameTV);
            commentTV = itemView.findViewById(R.id.row_comment_content);
            parentLayoutRL = itemView.findViewById(R.id.row_comment_parentLayout);
        }
    }
}
