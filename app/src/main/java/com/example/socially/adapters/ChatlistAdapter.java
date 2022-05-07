package com.example.socially.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.ContentInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.socially.ModelUser;
import com.example.socially.R;
import com.example.socially.UsersAdapter;
import com.example.socially.activities.ChatActivity;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;

public class ChatlistAdapter extends RecyclerView.Adapter<ChatlistAdapter.MyHolder>{

    Context context;
    List<ModelUser> userList; //get user info
    private HashMap<String, String> lastMessageMap;
    private UsersAdapter.RecyclerViewClickListener listener;

    public ChatlistAdapter(Context context, List<ModelUser> userList, UsersAdapter.RecyclerViewClickListener listener) {
        this.context = context;
        this.userList = userList;
        lastMessageMap = new HashMap<>();
        this.listener = listener;
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflate layout row_chatlist.xml
        View view = LayoutInflater.from(context).inflate(R.layout.row_chatlist, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {
        //get data
        String hisUid = userList.get(position).getUid();
        String userImage = userList.get(position).getProfileImage();
        String userName = userList.get(position).getFirstName() +" " + userList.get(position).getLastName();;
        String lastMessage = lastMessageMap.get(hisUid);

        //set data
        holder.nameTv.setText(userName);
        if (lastMessage == null || lastMessage.equals("default")) {
            holder.lastMessageTv.setVisibility(View.GONE);
        } else {
            holder.lastMessageTv.setVisibility(View.VISIBLE);
            holder.lastMessageTv.setText(lastMessage);
        }
        try {
            Glide.with(context).asBitmap().load(userImage).into(holder.profileIv);
//        get().load(userImage).placeholder(R.drawable.ic_default_image).into(holder.profileIv);
        }
        catch (Exception e) {
//            Glide.with(context).asBitmap().load(R.drawable.ic_default_image).into(holder.profileIv);
            holder.profileIv.setImageResource(R.drawable.ic_default_image);
//            Picasso.get().load(R.drawable.ic_default_image).into(holder.profileIv);
        }
        //set online status of other users in chatlist
        if (userList.get(position).getOnlineStatus().equals("online")) {
            //online
            holder.onlineStatusIv.setImageResource(R.drawable.circle_online);
        }
        else {
            //offline
            holder.onlineStatusIv.setImageResource(R.drawable.circle_offline);
        }

        //handle click of user in chatlist
//        holder.itemView.setOnClickListener(view -> {
////                Toast.makeText(context, hisUid, Toast.LENGTH_SHORT).show();
//            //start chat activity with that user
//            Intent intent = new Intent(context, ChatActivity.class);
//            intent.putExtra("hisUid", hisUid);
//            context.startActivity(intent);
//        }
//        );
    }

    public void setLastMessageMap(String userId, String lastMessage) {
        lastMessageMap.put(userId, lastMessage);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public interface RecyclerViewClickListener{
        void onClick(View v, int position);
    }

    class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        //views of row_chatlist.xml
        ImageView profileIv, onlineStatusIv;
        TextView nameTv, lastMessageTv;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //init views
            profileIv = itemView.findViewById(R.id.profileIv);
            onlineStatusIv = itemView.findViewById(R.id.onlineStatusIv);
            nameTv = itemView.findViewById(R.id.nameTv);
            lastMessageTv = itemView.findViewById(R.id.lastMessageTv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view, getBindingAdapterPosition());
        }
    }
}
