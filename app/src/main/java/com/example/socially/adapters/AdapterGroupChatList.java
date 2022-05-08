package com.example.socially.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socially.R;
import com.example.socially.models.ModelGroupChats;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterGroupChatList extends RecyclerView.Adapter<AdapterGroupChatList.HolderGroupChatList>{

    private Context context;
    private ArrayList<ModelGroupChats> groupChats;


    public AdapterGroupChatList(Context context, ArrayList<ModelGroupChats> groupChats) {
        this.context = context;
        this.groupChats = groupChats;
    }

    @NonNull
    @Override
    public HolderGroupChatList onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //inflatew layout
        View view = LayoutInflater.from(context).inflate(R.layout.row_groupchats_list,parent,false);
        return new HolderGroupChatList(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HolderGroupChatList holder, int position) {
        //get data
        ModelGroupChats model = groupChats.get(position);
        String groupId = model.getGroupId();
        String groupIcon = model.getGroupIcon();
        String groupTitle = model.getGroupTitle();

        //set data
        holder.groupTitleTv.setText(groupTitle);

        try{
            Picasso.get().load(groupIcon).placeholder(R.drawable.ic_group_white).into(holder.groupIconIv);
        }catch (Exception e){
            holder.groupIconIv.setImageResource(R.drawable.ic_group_white);
        }

        //hanlde group click
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //will do later
            }
        });

    }

    @Override
    public int getItemCount() {
        return groupChats.size();
    }

    //veiw holder class
    class HolderGroupChatList extends RecyclerView.ViewHolder {

        //ui views
        private ImageView groupIconIv;
        private TextView groupTitleTv,nameTv,messageTv,timeTv;

        public HolderGroupChatList(@NonNull View itemView) {
            super(itemView);

            groupIconIv = itemView.findViewById(R.id.groupIconIv);
            groupTitleTv = itemView.findViewById(R.id.groupTitleTv);
            nameTv = itemView.findViewById(R.id.nameTv);
            messageTv = itemView.findViewById(R.id.messageTv);
            timeTv = itemView.findViewById(R.id.timeTv);





        }
    }
}
