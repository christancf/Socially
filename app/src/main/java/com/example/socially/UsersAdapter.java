package com.example.socially;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder>{

    private Context context;
    private List<ModelUser> usersList;
    private RecyclerViewClickListener listener;

    public UsersAdapter(Context context, List<ModelUser> usersList, RecyclerViewClickListener listener) {
        this.context = context;
        this.usersList = usersList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UsersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_user, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull UsersAdapter.ViewHolder holder, int position) {
        String userImage = usersList.get(position).getProfileImage();
        String userName = usersList.get(position).getFirstName() + " " + usersList.get(position).getLastName();
        Glide.with(context)
                .asBitmap()
                .load(userImage)
                .into(holder.userImageCIV);

        holder.usernameTV.setText(userName);

    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public interface RecyclerViewClickListener{
        void onClick(View v, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        CircleImageView userImageCIV;
        TextView usernameTV;
        RelativeLayout parentLayoutRL;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userImageCIV = itemView.findViewById(R.id.userImage);
            usernameTV = itemView.findViewById(R.id.username);
            parentLayoutRL = itemView.findViewById(R.id.parent_layout);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view, getBindingAdapterPosition());
        }
    }
}
