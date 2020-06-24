package com.example.petmania.activities.doctorapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.petmania.R;
import com.example.petmania.activities.MessageActivity;
import com.example.petmania.activities.Notifications.FCMResponse;
import com.example.petmania.activities.Notifications.FCMSendData;
import com.example.petmania.activities.Notifications.IFCMService;
import com.example.petmania.activities.Notifications.RetrofitFCMClient;
import com.example.petmania.activities.Notifications.TokenModel;
import com.example.petmania.adapter.MessagesAdapter;
import com.example.petmania.model.Chats;
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
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class UserDocMessagesActivity extends AppCompatActivity {

    private TextView drNameTv, drStatus;
    private RecyclerView recyclerView;
    private EditText chatText;
    private ImageView sendBtn;
    private int drId,userId;
    private boolean isUser;
    DatabaseReference reference;
    private String drName,userName;
    MessagesAdapter adapter;
    List<Chats> chatsList;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference usersDbReference;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    boolean notify = false;
    IFCMService ifcmService;
    ValueEventListener seenListener;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_doc_messages);

        drId = getIntent().getIntExtra("dr_id", -1);
        drName = getIntent().getStringExtra("dr_name");
        userId = getIntent().getIntExtra("user_id", -1);
        userName = getIntent().getStringExtra("user_name");
        isUser = getIntent().getBooleanExtra("is_user", true);
        ifcmService = RetrofitFCMClient.getInstance().create(IFCMService.class);
        Toolbar toolbar = findViewById(R.id.toolbar_dr_chat);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        subscribeToTopic(Common.createTopicOrder());
        drNameTv = findViewById(R.id.dr_name_chat);
        drStatus = findViewById(R.id.drStatusTv);
        recyclerView = findViewById(R.id.recycler_messages_dr);
        chatText = findViewById(R.id.text_send_dr);
        sendBtn = findViewById(R.id.send_btn_dr);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        manager.setStackFromEnd(true);
        recyclerView.setLayoutManager(manager);


        firebaseDatabase = FirebaseDatabase.getInstance();
        usersDbReference = firebaseDatabase.getReference("Doctors");
        if (isUser) {
            Query query = usersDbReference.orderByChild("id").equalTo(String.valueOf(drId));
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String onlineStatus = "" + snapshot.child("onlineStatus").getValue();
                        String typingStatus = "" + snapshot.child("typingTo").getValue();
                        if (typingStatus.equals(String.valueOf(Common.currentUser.getUser_id()))) {
                            drStatus.setText("Typing...");
                        } else {
                            if (onlineStatus.equals("online")) {
                                drStatus.setText(onlineStatus);
                            } else {
                                GetTimeAgo getTimeAgo = new GetTimeAgo();
                                Long time = Long.parseLong(onlineStatus);
                                String dateTime = getTimeAgo.getTimeAgo(time, UserDocMessagesActivity.this);
                                drStatus.setText("last seen: " + dateTime);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            drNameTv.setText(drName);
            chatText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.toString().trim().length() == 0) {
                        checkTypingStatus("onOne");
                    } else {
                        checkTypingStatus(String.valueOf(drId));
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            readMessage(Common.currentUser.getUser_id(), drId);
            seenMessage(Common.currentUser.getUser_id(),drId);
        }else {
            //Doctor sy open hua ha
            Query query = usersDbReference.orderByChild("id").equalTo(String.valueOf(userId));
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String onlineStatus = "" + snapshot.child("onlineStatus").getValue();
                        String typingStatus = "" + snapshot.child("typingTo").getValue();
                        if (typingStatus.equals(String.valueOf(Common.currentDoctor.getId()))) {
                            drStatus.setText("Typing...");
                        } else {
                            if (onlineStatus.equals("online")) {
                                drStatus.setText(onlineStatus);
                            } else {
                                GetTimeAgo getTimeAgo = new GetTimeAgo();
                                Long time = Long.parseLong(onlineStatus);
                                String dateTime = getTimeAgo.getTimeAgo(time, UserDocMessagesActivity.this);
                                drStatus.setText("last seen: " + dateTime);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            drNameTv.setText(userName);
            chatText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    if (charSequence.toString().trim().length() == 0) {
                        checkTypingStatus("onOne");
                    } else {
                        checkTypingStatus(String.valueOf(userId));
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            readMessage(Common.currentDoctor.getId(), userId);
            seenMessage(Common.currentDoctor.getId(),userId);

        }

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notify = true;
                String msgtxt = chatText.getText().toString();
                if (!TextUtils.isEmpty(msgtxt)) {
                  if (isUser){
                      sendMessage(Common.currentUser.getUser_id(),drId,msgtxt);
                  }else {
                      sendMessage(Common.currentDoctor.getId(),userId,msgtxt);
                  }
                } else {
                    Toast.makeText(UserDocMessagesActivity.this, "Can't send empty message", Toast.LENGTH_SHORT).show();
                }
                chatText.setText("");
            }
        });
    }


    private void seenMessage(final int myId,int hisId){
        reference = FirebaseDatabase.getInstance().getReference("ChatsDr");
        seenListener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Chats chats= snapshot.getValue(Chats.class);
                    if (chats.getReciever()==myId&& chats.getSender()==hisId){
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
    private void readMessage(final int myId,final int userId){
        chatsList = new ArrayList<>();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("ChatsDr");
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
                    adapter= new MessagesAdapter(UserDocMessagesActivity.this,chatsList,String.valueOf(-1),isUser);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void sendMessage(int sender, int reciever, String msgtxt) {
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
//
//        HashMap<String, Object> hashMap = new HashMap<>();
//        hashMap.put("sender", Common.currentUser.getUser_id());
//        hashMap.put("reciever", drId);
//        hashMap.put("message", msgtxt);
//        hashMap.put("isseen", false);
//        hashMap.put("timestamp", String.valueOf(System.currentTimeMillis()));
//        reference.child("ChatsDr").push().setValue(hashMap);
//
//        chatText.setText("");
        if (isUser){
            FirebaseDatabase.getInstance().getReference("TokensDr")
                    .child(String.valueOf(drId)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        TokenModel tokenModel=dataSnapshot.getValue(TokenModel.class);
                        Map<String,String> notiData = new HashMap<>();
                        notiData.put(Common.NOTI_TITLE,"New Message");
                        notiData.put(Common.NOTI_CONTENT,""+Common.currentUser.getName()+": "+msgtxt);
                        notiData.put("is_user","false");
                        notiData.put("user_id", String.valueOf(sender));
                        notiData.put("dr_id",String.valueOf(drId));
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
                                        hashMap.put("message",msgtxt);
                                        hashMap.put("isseen",false);
                                        hashMap.put("timestamp",String.valueOf(System.currentTimeMillis()));
                                        databaseReference.child("ChatsDr").push().setValue(hashMap);

                                        DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("ChatlistDr")
                                                .child(String.valueOf(sender))
                                                .child(String.valueOf(reciever));
                                        chatRef1.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (!dataSnapshot.exists()){
                                                    chatRef1.child("id").setValue(reciever);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                        DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("ChatlistDr")
                                                .child(String.valueOf(reciever))
                                                .child(String.valueOf(sender));
                                        chatRef2.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (!dataSnapshot.exists()){
                                                    chatRef2.child("id").setValue(sender);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }, new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {
                                        Toast.makeText(UserDocMessagesActivity.this, "Order Send But Notification failed due to  "+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }));
                    }else {
                        Toast.makeText(UserDocMessagesActivity.this, "Token Not Found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else {

            FirebaseDatabase.getInstance().getReference("Tokens")
                    .child(String.valueOf(userId)).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()){
                        TokenModel tokenModel=dataSnapshot.getValue(TokenModel.class);
                        Map<String,String> notiData = new HashMap<>();
                        notiData.put(Common.NOTI_TITLE,"New Message From Dr ");
                        notiData.put(Common.NOTI_CONTENT,""+Common.currentDoctor.getDr_name()+": "+msgtxt);
                        notiData.put("is_user","false");
                        notiData.put("user_id", String.valueOf(userId));
                        notiData.put("dr_id",String.valueOf(Common.currentDoctor.getId()));
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
                                        hashMap.put("message",msgtxt);
                                        hashMap.put("isseen",false);
                                        hashMap.put("timestamp",String.valueOf(System.currentTimeMillis()));
                                        databaseReference.child("ChatsDr").push().setValue(hashMap);

                                        DatabaseReference chatRef1 = FirebaseDatabase.getInstance().getReference("ChatlistDr")
                                                .child(String.valueOf(sender))
                                                .child(String.valueOf(reciever));
                                        chatRef1.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (!dataSnapshot.exists()){
                                                    chatRef1.child("id").setValue(reciever);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                        DatabaseReference chatRef2 = FirebaseDatabase.getInstance().getReference("ChatlistDr")
                                                .child(String.valueOf(reciever))
                                                .child(String.valueOf(sender));
                                        chatRef2.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (!dataSnapshot.exists()){
                                                    chatRef2.child("id").setValue(sender);
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                }, new Consumer<Throwable>() {
                                    @Override
                                    public void accept(Throwable throwable) throws Exception {
                                        Toast.makeText(UserDocMessagesActivity.this, "Order Send But Notification failed due to  "+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }));
                    }else {
                        Toast.makeText(UserDocMessagesActivity.this, "Token Not Found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        String timeStamp = String.valueOf(System.currentTimeMillis());
        checkOnlineStatus(timeStamp);
        checkTypingStatus("onOne");
        reference.removeEventListener(seenListener);
        currentChatDr("none");
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkOnlineStatus("online");
        if (isUser){
            currentChatDr(String.valueOf(drId));
        }else {
            currentChatDr(String.valueOf(userId));
        }
    }

    private void currentChatDr(String userId) {
        SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
        editor.putString("currentChatUser", userId);
        editor.apply();
    }

    private void subscribeToTopic(String topicOrder) {
        FirebaseMessaging.getInstance()
                .subscribeToTopic(topicOrder)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(UserDocMessagesActivity.this, "failure " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(UserDocMessagesActivity.this, "" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (isUser){
            HashMap<Object,String> userMap = new HashMap<>();
            userMap.put("id", String.valueOf(Common.currentUser.getUser_id()));
            userMap.put("onlineStatus","online");
            userMap.put("typingTo","noOne");
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference reference = database.getReference("Doctors");
            reference.child(String.valueOf(Common.currentUser.getUser_id())).setValue(userMap);
        }
        checkOnlineStatus("online");
    }

    private void checkOnlineStatus(String status) {
        if (isUser){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Doctors").child(String.valueOf(Common.currentUser.getUser_id()));
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("onlineStatus", status);
            reference.updateChildren(hashMap);
        }else {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Doctors").child(String.valueOf(Common.currentDoctor.getId()));
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("onlineStatus", status);
            reference.updateChildren(hashMap);
        }
    }

    private void checkTypingStatus(String typing) {
        if (isUser){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Doctors").child(String.valueOf(Common.currentUser.getUser_id()));
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("typingTo", typing);
            reference.updateChildren(hashMap);
        }else {
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Doctors").child(String.valueOf(Common.currentDoctor.getId()));
            HashMap<String, Object> hashMap = new HashMap<>();
            hashMap.put("typingTo", typing);
            reference.updateChildren(hashMap);
        }
    }
}