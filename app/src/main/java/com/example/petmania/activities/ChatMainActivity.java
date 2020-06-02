package com.example.petmania.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;

import com.example.petmania.R;
import com.example.petmania.fragments.ChatMainFragment;
import com.example.petmania.fragments.ChatsFragment;
import com.example.petmania.model.Chats;
import com.example.petmania.utils.Common;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ChatMainActivity extends AppCompatActivity {

    private DatabaseReference reference;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_main);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);

        reference = FirebaseDatabase.getInstance().getReference("Chats");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ViewPageAdapter viewPageAdapter =new ViewPageAdapter(getSupportFragmentManager());
                int unread =0;
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Chats chats = snapshot.getValue(Chats.class);
                    if (chats.getReciever()== Common.currentUser.getUser_id() && !chats.isIsseen()){
                        unread++;
                    }
                }
                if (unread==0){
                    viewPageAdapter.addFragment(new ChatsFragment(),"inbox");
                }else {
                    viewPageAdapter.addFragment(new ChatsFragment(), "(" + unread + ") unread Messages");
                }
                viewPager.setAdapter(viewPageAdapter);
                tabLayout.setupWithViewPager(viewPager);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    class ViewPageAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        ViewPageAdapter(FragmentManager fm) {
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment, String title){
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }
}
