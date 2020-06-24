package com.example.petmania.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.petmania.R;
import com.example.petmania.activities.Notifications.FCMResponse;
import com.example.petmania.activities.Notifications.FCMSendData;
import com.example.petmania.activities.Notifications.IFCMService;
import com.example.petmania.activities.Notifications.RetrofitFCMClient;
import com.example.petmania.activities.Notifications.TokenModel;
import com.example.petmania.adapter.MessagesAdapter;
import com.example.petmania.model.Adds;
import com.example.petmania.model.Chats;
import com.example.petmania.model.User;
import com.example.petmania.utils.Common;
import com.example.petmania.utils.GetTimeAgo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.grpc.Server;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MessageActivity extends AppCompatActivity {

    private TextView usernametv;
    private int user_id;
    private int ad_id;
    private EditText msg;
    private ImageButton sendBtn;
    MessagesAdapter adapter;
    List<Chats> chatsList;
    RecyclerView recyclerView;
    DatabaseReference reference;
    IFCMService ifcmService;
    TextView userStatusTv;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference usersDbReference;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    boolean notify = false;
    ValueEventListener seenListener;
    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }

    private TextView adInfoTitle,adInfoPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        firebaseDatabase = FirebaseDatabase.getInstance();
        usersDbReference = firebaseDatabase.getReference("Users");

        ifcmService = RetrofitFCMClient.getInstance().create(IFCMService.class);
        Toolbar toolbar= findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        user_id=getIntent().getIntExtra("user_id",-1);
        ad_id = getIntent().getIntExtra("ad_id",-1);
        subscribeToTopic(Common.createTopicOrder());

        userStatusTv = findViewById(R.id.userStatusTv);
        usernametv = findViewById(R.id.username);
        msg = findViewById(R.id.text_send);
        sendBtn = findViewById(R.id.send_btn);
        adInfoTitle = findViewById(R.id.ad_info_title);
        adInfoPrice = findViewById(R.id.ad_info_price);


        recyclerView = findViewById(R.id.recycler_messages);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        manager.setStackFromEnd(true);
        recyclerView.setLayoutManager(manager);
        if (user_id!= -1) {
            Common.getAPI().getUserByUserId(String.valueOf(user_id)).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    User user = response.body();
                    if (TextUtils.isEmpty(user.getError_msg())) {
                        usernametv.setText(user.getName());
                    } else {
                        Toast.makeText(MessageActivity.this, "Error" + user.getError_msg(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(MessageActivity.this, "THROW "+t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        if (ad_id!=-1){
            Common.getAPI().getAdsByAdId(ad_id)
                    .enqueue(new Callback<Adds>() {
                        @Override
                        public void onResponse(Call<Adds> call, Response<Adds> response) {
                            Adds adds = response.body();
                            if (TextUtils.isEmpty(adds.getError_msg())){
                                adInfoTitle.setText(adds.getAdd_title());
                                adInfoPrice.setText(new StringBuilder(adds.getAdd_price()).append(" Rs."));
                            }
                        }

                        @Override
                        public void onFailure(Call<Adds> call, Throwable t) {

                        }
                    });
        }

        Query query = usersDbReference.orderByChild("uid").equalTo(user_id);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren()) {
                    String onlineStatus = "" + snapshot.child("onlineStatus").getValue();
                    String typingStatus = "" + snapshot.child("typingTo").getValue();

                    if (typingStatus.equals(String.valueOf(Common.currentUser.getUser_id()))) {
                        userStatusTv.setText("Typing...");
                    } else {
                        if (onlineStatus.equals("online")) {
                            userStatusTv.setText(onlineStatus);
                        } else {
                            GetTimeAgo getTimeAgo = new GetTimeAgo();
                            Long time = Long.parseLong(onlineStatus);
                            String dateTime = getTimeAgo.getTimeAgo(time,MessageActivity.this);
                            userStatusTv.setText("last seen: " + dateTime);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                String msgtxt = msg.getText().toString();
                if (!TextUtils.isEmpty(msgtxt)){
                    sendMessage(Common.currentUser.getUser_id(),user_id,msgtxt);
                }else {
                    Toast.makeText(MessageActivity.this, "Can't send empty message", Toast.LENGTH_SHORT).show();
                }
                msg.setText("");
            }
        });
        msg.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length()==0){
                    checkTypingStatus("onOne");
                }else {
                    checkTypingStatus(String.valueOf(user_id));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        Log.d("MessageActivity", "onCreate: ###IDNICHAY "+user_id);
        readMessage(Common.currentUser.getUser_id(),user_id,"");
        seenMessage(String.valueOf(user_id));
    }

    private void sendMessage(int sender, int reciever, String message) {

        FirebaseDatabase.getInstance().getReference("Tokens")
                .child(String.valueOf(reciever)).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    TokenModel tokenModel=dataSnapshot.getValue(TokenModel.class);
                    Map<String,String> notiData = new HashMap<>();
                    notiData.put(Common.NOTI_TITLE,"New Message");
                    notiData.put(Common.NOTI_CONTENT,""+Common.currentUser.getName()+": "+message);
                    notiData.put("is_user","true");
                    notiData.put("user_id", String.valueOf(sender));
                    notiData.put("ad_id",String.valueOf(ad_id));
                    FCMSendData sendData = new FCMSendData(tokenModel.getToken(),notiData);
                    compositeDisposable.add(ifcmService.sendNotification(sendData)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<FCMResponse>() {
                                @Override
                                public void accept(FCMResponse fcmResponse) throws Exception {
                                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                                    HashMap<String,Object> hashMap = new HashMap<>();
                                    hashMap.put("sender",sender);
                                    hashMap.put("reciever",reciever);
                                    hashMap.put("message",message);
                                    hashMap.put("isseen",false);
                                    hashMap.put("timestamp",String.valueOf(System.currentTimeMillis()));
                                    databaseReference.child("Chats").child(String.valueOf(ad_id)).push().setValue(hashMap);

                                    DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference();
                                    chatRef.child("Chatlist").child(String.valueOf(sender));
                                    chatRef.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                           // if (!dataSnapshot.hasChild(String.valueOf(user_id))){
                                                Map chatMap1 = new HashMap();
                                                chatMap1.put("id",reciever);
                                                chatMap1.put("ad_id",ad_id);

                                                Map chatMap2 = new HashMap();
                                                chatMap2.put("id",sender);
                                                chatMap2.put("ad_id",ad_id);

                                                Map userMap =new HashMap();
                                                userMap.put("Chatlist/"+ sender +"/"+ad_id,chatMap1);
                                                userMap.put("Chatlist/"+ reciever +"/"+ad_id,chatMap2);

                                                chatRef.updateChildren(userMap, new DatabaseReference.CompletionListener() {
                                                    @Override
                                                    public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                                                        if (databaseError!=null){
                                                            Toast.makeText(MessageActivity.this, "DATABASE ERROR "+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });
//                                    DatabaseReference chatRef = FirebaseDatabase.getInstance().getReference("Chatlist")
//                                            .child(String.valueOf(sender))
//                                            .child(String.valueOf(reciever));
//                                    chatRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                                            if (!dataSnapshot.exists()){
//                                                chatRef.child("id").setValue(user_id);
//                                                chatRef.child("ad").setValue(ad_id);
//                                            }
//                                        }
//
//                                        @Override
//                                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                                        }
//                                    });


                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {
                                    Toast.makeText(MessageActivity.this, "Order Send But Notification failed due to  "+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }));
                }else {
                    Toast.makeText(MessageActivity.this, "Token Not Found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void readMessage(final int myId,final int userId,final String imgUrl){
        chatsList = new ArrayList<>();
        reference= FirebaseDatabase.getInstance().getReference("Chats").child(String.valueOf(ad_id));
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatsList.clear();
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Chats chats =snapshot.getValue(Chats.class);
                    if (chats.getReciever()==myId && chats.getSender()==userId ||
                    chats.getReciever()==userId && chats.getSender()==myId){
                        chatsList.add(chats);
                    }
                    adapter= new MessagesAdapter(MessageActivity.this,chatsList,String.valueOf(ad_id),true);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void seenMessage(final String userId){
        reference = FirebaseDatabase.getInstance().getReference("Chats").child(String.valueOf(ad_id));
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Chats chats= snapshot.getValue(Chats.class);
                    if (chats.getReciever()==Common.currentUser.getUser_id()&& chats.getSender()==user_id){
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("isseen",true);
                        snapshot.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        String timeStamp = String.valueOf(System.currentTimeMillis());
        checkOnlineStatus(timeStamp);
        checkTypingStatus("onOne");
        reference.removeEventListener(seenListener);
        currentChatUser("none");
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkOnlineStatus("online");
        currentChatUser(String.valueOf(user_id));
    }

    private void currentChatUser(String userId){
        SharedPreferences.Editor editor = getSharedPreferences("PREFS",MODE_PRIVATE).edit();
        editor.putString("currentChatUser",userId);
        editor.apply();
    }

    
    private void subscribeToTopic(String topicOrder) {
        FirebaseMessaging.getInstance()
                .subscribeToTopic(topicOrder)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MessageActivity.this, "failure "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()){
                    Toast.makeText(MessageActivity.this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        checkOnlineStatus("online");
    }

    private void checkOnlineStatus(String status){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(String.valueOf(Common.currentUser.getUser_id()));
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("onlineStatus",status);
        reference.updateChildren(hashMap);
    }

    private void checkTypingStatus(String typing){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(String.valueOf(Common.currentUser.getUser_id()));
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("typingTo",typing);
        reference.updateChildren(hashMap);
    }
}
