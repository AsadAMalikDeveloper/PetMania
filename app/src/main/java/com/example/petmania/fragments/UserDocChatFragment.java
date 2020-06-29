package com.example.petmania.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.petmania.R;
import com.example.petmania.adapter.ChatsConversationAdapter;
import com.example.petmania.adapter.ChatsDrConversationAdapter;
import com.example.petmania.model.Adds;
import com.example.petmania.model.Chatlist;
import com.example.petmania.model.Doctors;
import com.example.petmania.utils.Common;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class UserDocChatFragment extends Fragment {




    public UserDocChatFragment() {
        // Required empty public constructor
    }

    private RecyclerView recyclerView;
    private List<Chatlist> usersListId;
    DatabaseReference reference;
    ChatsDrConversationAdapter userAdapter;
    List<Doctors> userList;
    int user_id =-1;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_doc_chat, container, false);

        recyclerView  = view.findViewById(R.id.recycler_dr_chats);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        usersListId = new ArrayList<>();

        load();
        return  view;
    }

    private void load() {
        reference= FirebaseDatabase.getInstance().getReference("ChatlistDr").child(String.valueOf(Common.currentUser.getUser_id()));
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Chatlist chatlist = snapshot.getValue(Chatlist.class);
                    usersListId.add(chatlist);
                }
                chatList();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void chatList() {
//        userList = new ArrayList<>();
//        compositeDisposable.add(Common.getAPI().getAllUsers()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(users -> {
//                    userList.clear();
//                    for (User user:users){
//                        for (Chatlist chatlist:usersListId){
//                            if (user.getUser_id()==chatlist.id){
//                                userList.add(user);
//                            }
//                        }
//                    }
//
//                    userAdapter = new ChatUserAdapter(getContext(),userList);
//                    recyclerView.setAdapter(userAdapter);
//                }));
        userList = new ArrayList<>();
        compositeDisposable.add(Common.getAPI().loadDoctors()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(doctors -> {
                    userList.clear();
                    for (Doctors doctors1:doctors){
                        for (Chatlist chatlist:usersListId){
                            if (doctors1.getId()==chatlist.id){
                                userList.add(doctors1);
                            }
                        }
                    }

                    userAdapter = new ChatsDrConversationAdapter(getContext(),userList);
                    recyclerView.setAdapter(userAdapter);
                }));
    }
}