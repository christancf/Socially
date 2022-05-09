package com.example.socially.adapters;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.socially.EditCommentActivity;
import com.example.socially.ModelUser;
import com.example.socially.R;
import com.example.socially.models.ModelComment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
    private String pID;

    private final String firebaseURL = "https://socially-14fd2-default-rtdb.asia-southeast1.firebasedatabase.app";
    public CommentAdapter(Context context, List<ModelComment> commentsList, String pID) {
        this.context = context;
        this.commentsList = commentsList;
        this.pID = pID;
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

        if(!uid.equals(FirebaseAuth.getInstance().getUid())){
            holder.editTV.setVisibility(View.GONE);
            holder.deleteTV.setVisibility(View.GONE);
        }else{
            holder.editTV.setOnClickListener(view -> {
                Intent intent = new Intent(context, EditCommentActivity.class);
                intent.putExtra("commentID", cid);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            });

            holder.deleteTV.setOnClickListener(view -> showDeleteConfirmDialog(view, cid));
        }

    }

    private void showDeleteConfirmDialog(View view, String cid) {
        final Dialog deleteDialog = new Dialog(view.getContext());
        deleteDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        deleteDialog.setContentView(R.layout.alert_dialog_confirm_delete);

        TextView cancel = deleteDialog.findViewById(R.id.comment_delete_cancelTV);
        TextView delete = deleteDialog.findViewById(R.id.comment_delete_confirmTV);

        cancel.setOnClickListener(v -> deleteDialog.dismiss());
        delete.setOnClickListener(v -> {
            deleteDialog.dismiss();
            deleteComment(cid);
            Toast.makeText(context, "Comment deleted successfully", Toast.LENGTH_SHORT).show();
        });

        deleteDialog.show();
        deleteDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        deleteDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        deleteDialog.getWindow().getAttributes().windowAnimations = R.style.alertDialogAnimation;
        deleteDialog.getWindow().setGravity(Gravity.CENTER);
    }

    private void deleteComment(String cid) {
        DatabaseReference ref = FirebaseDatabase.getInstance(firebaseURL)
                                    .getReference("Posts").child(pID);
        ref.child("Comments").child(cid).removeValue();

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String comments = "" + snapshot.child("pComments").getValue();
                int newCommentVal = Integer.parseInt(comments) - 1;
                ref.child("pComments").setValue(""+newCommentVal);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    @Override
    public int getItemCount() {
        return commentsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        CircleImageView userImageCIV;
        TextView usernameTV, commentTV, editTV, deleteTV;
        RelativeLayout parentLayoutRL;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userImageCIV = itemView.findViewById(R.id.row_comment_userImageIV);
            usernameTV = itemView.findViewById(R.id.row_comment_usernameTV);
            commentTV = itemView.findViewById(R.id.row_comment_content);
            editTV = itemView.findViewById(R.id.row_comment_edit);
            deleteTV = itemView.findViewById(R.id.row_comment_delete);
            parentLayoutRL = itemView.findViewById(R.id.row_comment_parentLayout);
        }
    }
}
