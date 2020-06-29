package com.example.petmania.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.petmania.R;
import com.example.petmania.activities.doctorapp.UserDocMessagesActivity;
import com.example.petmania.model.Chats;
import com.example.petmania.model.User;
import com.example.petmania.utils.Common;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsDrConversationDrAdapter extends RecyclerView.Adapter<ChatsDrConversationDrAdapter.ViewHolder>{

    private Context context;
    private List<User> userList;
    private String theLastMessage;

    public ChatsDrConversationDrAdapter(Context context, List<User> userList) {
        this.context = context;
        this.userList = userList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_dr_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context).load(R.drawable.app_icon_foreground).into(holder.profile);
        holder.username.setText(userList.get(position).getName());
        lastMessage(userList.get(position).getUser_id(),holder.lastMsg);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserDocMessagesActivity.class);
                intent.putExtra("user_name",userList.get(position).getName());
                intent.putExtra("is_user",false);
                intent.putExtra("user_id",userList.get(position).getUser_id());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView username,lastMsg;
        private CircleImageView profile;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username_dr);
            lastMsg = itemView.findViewById(R.id.last_msg_dr);
            profile = itemView.findViewById(R.id.profile_image_dr);
        }
    }
    private void lastMessage(int userID,TextView lastMsgg){
        theLastMessage = "default";
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("ChatsDr");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Chats chats = snapshot.getValue(Chats.class);
                    if (chats.getReciever()== Common.currentDoctor.getId() && chats.getSender()==userID
                            || chats.getReciever()==userID && chats.getSender()==Common.currentDoctor.getId()){
                        theLastMessage = chats.getMessage();
                    }
                }
                switch (theLastMessage){
                    case "default":
                        lastMsgg.setText("No Message Yet");
                        break;
                    default:
                        lastMsgg.setText(theLastMessage);
                        break;
                }
                theLastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
