package com.example.petmania.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.petmania.R;
import com.example.petmania.model.Chatlist;
import com.example.petmania.model.Chats;
import com.example.petmania.utils.Common;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.ViewHolder> {
    public static final int MSG_TYPE_LEFT =0;
    public static final int MSG_TYPE_RIGHT =1;

    private Context context;
    private List<Chats>chatsList;
    private String imageUrl;
    private String ad_id;

    public MessagesAdapter(Context context, List<Chats> chatsList, String ad_id) {
        this.context = context;
        this.chatsList = chatsList;
        this.ad_id= ad_id;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right,parent,false);
            return new ViewHolder(view);
        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left,parent,false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Chats chats=chatsList.get(position);
        String message = chats.getMessage();
        String timestamp = chats.getTimestamp();

        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(Long.parseLong(timestamp));
        String dateTime = DateFormat.format("dd/MM/yyyy hh:mm aa",calendar).toString();
        holder.messageTv.setText(message);
        holder.timeTv.setText(dateTime);
        try {
            Picasso.get().load(R.drawable.ic_default).placeholder(R.drawable.ic_default).into(holder.profileTv);
        }catch (Exception e){
            Picasso.get().load(R.drawable.ic_default).into(holder.profileTv);
        }
        if (position==chatsList.size()-1){
            if (chats.isIsseen()){
                holder.isSeenTv.setText("Seen");
            }else {
                holder.isSeenTv.setText("Delivered");
            }
        }else {
            holder.isSeenTv.setVisibility(View.GONE);
        }
        
        holder.messageLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete Message");
                builder.setMessage("are you sure you want to delete this message?");
                builder.setPositiveButton("delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        deleteMessage(position);
                    }
                });
                builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.create().show();
                return false;
            }
        });
    }

    private void deleteMessage(int position) {

        String messageTimeStamp = chatsList.get(position).getTimestamp();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats").child(ad_id);
        Query query = reference.orderByChild("timestamp").equalTo(messageTimeStamp);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Chats chats = snapshot.getValue(Chats.class);
                    if (chats.getSender() == Common.currentUser.getUser_id()) {
                        //snapshot.getRef().removeValue();

                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("message", "this message was deleted");
                        snapshot.getRef().updateChildren(hashMap);
                    }else {
                        Toast.makeText(context, "you only delete your messages"+Common.currentUser.getUser_id() , Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return chatsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView profileTv;
        TextView messageTv,timeTv,isSeenTv;
        LinearLayout messageLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileTv = itemView.findViewById(R.id.profileTv);
            messageTv = itemView.findViewById(R.id.messageTv);
            timeTv = itemView.findViewById(R.id.timeTv);
            isSeenTv = itemView.findViewById(R.id.isSeenTv);
            messageLayout = itemView.findViewById(R.id.messageLayout);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (chatsList.get(position).getSender()== Common.currentUser.getUser_id()){
            return MSG_TYPE_RIGHT;
        }else {
            return MSG_TYPE_LEFT;
        }
    }
}
