package com.example.petmania.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.petmania.R;
import com.example.petmania.activities.MessageActivity;
import com.example.petmania.model.Adds;
import com.example.petmania.model.Chatlist;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatsConversationAdapter extends RecyclerView.Adapter<ChatsConversationAdapter.ViewHolder> {
    private Context context;
    private List<User> userList;
    private List<Adds> addsList;
    private String theLastMessage;
    private int ad_id =-1,user_id =-1;

    public ChatsConversationAdapter(Context context, List<Adds> addsList,int user_id) {
        this.context = context;
        this.userList = userList;
        this.addsList = addsList;
        this.user_id = user_id;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_user_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //

        Adds adds = addsList.get(position);
        if (Common.currentUser.getUser_id()==adds.getUser_id()){
            holder.userName.setText("Ad by You");
            holder.username.setText(adds.getAdd_title());
            lastMessage(adds.getUser_id(), adds.getAdds_id(),holder.lastMsg);

            Glide.with(context)
                    .load("https://petsmaniapk.000webhostapp.com/functions/images/uploads/".concat(Common.currentUser.getPhone()).concat("_")
                            .concat(String.valueOf(addsList.get(position).getAdds_id())).concat("_").concat(String.valueOf(0)).concat(".jpg"))
                    .apply(new RequestOptions().placeholder(R.drawable.load))
                    .into(holder.profileImg);


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (user_id!=-1){
                        Intent intent = new Intent(context, MessageActivity.class);
                        intent.putExtra("user_id",user_id);
                        intent.putExtra("ad_id",addsList.get(position).getAdds_id());
                        context.startActivity(intent);
                    }
                }
            });

        }else{

            holder.username.setText(adds.getAdd_title());
            lastMessage(adds.getUser_id(), adds.getAdds_id(),holder.lastMsg);




            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, MessageActivity.class);
                    intent.putExtra("user_id",addsList.get(position).getUser_id());
                    intent.putExtra("ad_id",addsList.get(position).getAdds_id());
                    context.startActivity(intent);
                }
            });
            Common.getAPI().getUserByUserId(String.valueOf(adds.getUser_id())).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    User user = response.body();
                    if (TextUtils.isEmpty(user.getError_msg())){
                        holder.userName.setText(user.getName());

                        Glide.with(context)
                                .load("https://petsmaniapk.000webhostapp.com/functions/images/uploads/".concat(user.getPhone()).concat("_")
                                        .concat(String.valueOf(addsList.get(position).getAdds_id())).concat("_").concat(String.valueOf(0)).concat(".jpg"))
                                .apply(new RequestOptions().placeholder(R.drawable.load))
                                .into(holder.profileImg);
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {

                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return addsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView username;
        private CircleImageView profileImg;

        private TextView lastMsg,userName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            lastMsg = itemView.findViewById(R.id.last_msg);
            profileImg = itemView.findViewById(R.id.profile_image);
            userName = itemView.findViewById(R.id.user_name);

        }
    }
    private void lastMessage(int userID,int ad_id,TextView lastMsgg){
        theLastMessage = "default";
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats").child(String.valueOf(ad_id));
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
