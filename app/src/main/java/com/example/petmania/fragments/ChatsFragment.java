package com.example.petmania.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.petmania.R;
import com.example.petmania.adapter.ChatsConversationAdapter;
import com.example.petmania.model.Adds;
import com.example.petmania.model.Chatlist;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatsFragment extends Fragment {

    public ChatsFragment() {
        // Required empty public constructor
    }
    private RecyclerView recyclerView;
    private List<Chatlist> usersListId;
    DatabaseReference reference;
    ChatsConversationAdapter userAdapter;
    List<Adds> userList;
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
        View view= inflater.inflate(R.layout.fragment_chats, container, false);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        recyclerView = view.findViewById(R.id.recycler_chats);
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


//        reference.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                usersListId.clear();
//                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
//
//                    Chats chats = snapshot.getValue(Chats.class);
//                    if (chats.getSender()== Common.currentUser.getUser_id()){
//                        usersListId.add(chats.getReciever());
//                    }if (chats.getReciever()== Common.currentUser.getUser_id()){
//                        usersListId.add(chats.getSender());
//                    }
//                }
//                readChats();
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

        return view;
    }

    private void load() {
        reference= FirebaseDatabase.getInstance().getReference("Chatlist").child(String.valueOf(Common.currentUser.getUser_id()));
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
        compositeDisposable.add(Common.getAPI().loadAds()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(addsList -> {
                    userList.clear();
                    for (Adds adds:addsList){
                        for (Chatlist chatlist:usersListId){
                            if (adds.getAdds_id()==chatlist.ad_id){
                                user_id =chatlist.getId();
                                userList.add(adds);
                            }
                        }
                    }

                    userAdapter = new ChatsConversationAdapter(getContext(),userList,user_id);
                    recyclerView.setAdapter(userAdapter);
                }));
    }

    /*private void readChats() {
        userList = new ArrayList<>();
        compositeDisposable.add(Common.getAPI().getAllUsers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(users -> {
                    userList.clear();
                    for (User user:users){
                        Log.d("ChatsFragment", "readChats: ###ID "+user.getUser_id());
                        for (int id:usersListId){
                            if (user.getUser_id()==id){
                                if (userList.size()!=0){
                                    for (User user1:userList){
                                        if (user.getUser_id()!=user1.getUser_id()){
                                            userList.add(user);
                                        }
                                    }
                                }else {
                                    userList.add(user);
                                }
                            }
                        }
                    }
                    userAdapter = new ChatUserAdapter(getContext(),userList);
                    recyclerView.setAdapter(userAdapter);
                }));
    }*/

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

    }
}
