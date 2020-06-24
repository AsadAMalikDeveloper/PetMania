package com.example.petmania.activities.doctorapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.petmania.R;
import com.example.petmania.adapter.ChatsConversationAdapter;
import com.example.petmania.adapter.ChatsDrConversationAdapter;
import com.example.petmania.adapter.ChatsDrConversationDrAdapter;
import com.example.petmania.model.Adds;
import com.example.petmania.model.Chatlist;
import com.example.petmania.model.User;
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

public class DoctorsChatFragment extends Fragment {


    public DoctorsChatFragment() {
        // Required empty public constructor
    }

    private RecyclerView recyclerView;
    private List<Chatlist> usersListId;
    DatabaseReference reference;
    ChatsDrConversationDrAdapter userAdapter;
    List<User> userList;
    SwipeRefreshLayout swipeRefreshLayout;
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
        View view= inflater.inflate(R.layout.fragment_doctors_chat, container, false);

        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout_dr);
        recyclerView = view.findViewById(R.id.recycler_chats_dr);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        usersListId = new ArrayList<>();

        load();
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                chatList();
            }
        });

        return view;
    }

    private void load() {
        reference= FirebaseDatabase.getInstance().getReference("ChatlistDr").child(String.valueOf(Common.currentDoctor.getId()));
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
        swipeRefreshLayout.setRefreshing(false);
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
        compositeDisposable.add(Common.getAPI().loadUsers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(users -> {
                    userList.clear();
                    for (User user:users){
                        for (Chatlist chatlist:usersListId){
                            if (user.getUser_id()==chatlist.id){
                                userList.add(user);
                            }
                        }
                    }

                    userAdapter = new ChatsDrConversationDrAdapter(getContext(),userList);
                    recyclerView.setAdapter(userAdapter);
                }));
    }

}