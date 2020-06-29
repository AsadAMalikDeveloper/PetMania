package com.example.petmania.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petmania.R;
import com.example.petmania.activities.doctorapp.UserDocMessagesActivity;
import com.example.petmania.model.Chats;
import com.example.petmania.model.Doctors;
import com.example.petmania.model.User;
import com.example.petmania.utils.Common;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatsDrConversationAdapter extends RecyclerView.Adapter<ChatsDrConversationAdapter.ViewHolder> {
    private Context context;
    private List<Doctors>doctorsList;
    private String theLastMessage;

    public ChatsDrConversationAdapter(Context context, List<Doctors> doctorsList) {
        this.context = context;
        this.doctorsList = doctorsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_dr_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.username.setText(doctorsList.get(position).getDr_name());
        lastMessage(doctorsList.get(position).getId(),holder.lastMsg);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserDocMessagesActivity.class);
                intent.putExtra("dr_name",doctorsList.get(position).getDr_name());
                intent.putExtra("is_user",true);
                intent.putExtra("dr_id",doctorsList.get(position).getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return doctorsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView username,lastMsg;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username_dr);
            lastMsg = itemView.findViewById(R.id.last_msg_dr);
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
                    if (chats.getReciever()== Common.currentUser.getUser_id() && chats.getSender()==userID
                            || chats.getReciever()==userID && chats.getSender()==Common.currentUser.getUser_id()){
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
