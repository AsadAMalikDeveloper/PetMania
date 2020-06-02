package com.example.petmania.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.petmania.R;
import com.example.petmania.adapter.CategoryAdapter;
import com.example.petmania.utils.Common;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class NewFeedsActivity extends AppCompatActivity {

    private ImageButton backbtn;
    private TextView tabAllPetsTv,tabFavTv,tabSuggTv;
    private RelativeLayout allRl,favRl,suggRl;
    private TextView nameTv,emailTv;
    private GridView gridView;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_feeds);
        backbtn = findViewById(R.id.back_btn);

        nameTv = findViewById(R.id.nameTv);
        emailTv  = findViewById(R.id.emailTv);
        tabAllPetsTv = findViewById(R.id.tabAllTv);
        tabFavTv = findViewById(R.id.tabfavoritesTv);
        tabSuggTv = findViewById(R.id.tabSuggest);
        gridView = findViewById(R.id.grid_view);
        allRl = findViewById(R.id.allRl);
        favRl = findViewById(R.id.favRl);
        suggRl = findViewById(R.id.sugRl);

        init();


        showAll();
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tabAllPetsTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAll();
            }
        });

        tabFavTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFav();
            }
        });

        tabSuggTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSugg();
            }
        });
    }

    private void init() {
        nameTv.setText(Common.currentUser.getName());
        emailTv.setText(Common.currentUser.getEmail());
    }

    private void showSugg() {
        allRl.setVisibility(View.GONE);
        favRl.setVisibility(View.GONE);
        suggRl.setVisibility(View.VISIBLE);


        tabAllPetsTv.setTextColor(getResources().getColor(R.color.white));
        tabAllPetsTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        tabFavTv.setTextColor(getResources().getColor(R.color.white));
        tabFavTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        tabSuggTv.setTextColor(getResources().getColor(R.color.black));
        tabSuggTv.setBackgroundResource(R.drawable.shape_rect04);
    }

    private void showFav() {
        allRl.setVisibility(View.GONE);
        favRl.setVisibility(View.VISIBLE);
        suggRl.setVisibility(View.GONE);


        tabAllPetsTv.setTextColor(getResources().getColor(R.color.white));
        tabAllPetsTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        tabSuggTv.setTextColor(getResources().getColor(R.color.white));
        tabSuggTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        tabFavTv.setTextColor(getResources().getColor(R.color.black));
        tabFavTv.setBackgroundResource(R.drawable.shape_rect04);
    }

    private void showAll() {

        allRl.setVisibility(View.VISIBLE);
        favRl.setVisibility(View.GONE);
        suggRl.setVisibility(View.GONE);

        tabAllPetsTv.setTextColor(getResources().getColor(R.color.black));
        tabAllPetsTv.setBackgroundResource(R.drawable.shape_rect04);

        tabFavTv.setTextColor(getResources().getColor(R.color.white));
        tabFavTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));
        tabSuggTv.setTextColor(getResources().getColor(R.color.white));
        tabSuggTv.setBackgroundColor(getResources().getColor(android.R.color.transparent));

        showCat();
    }
    private void showCat() {
        compositeDisposable.add(Common.getAPI().getCategories()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(categoties -> {
                    CategoryAdapter categoryAdapter = new CategoryAdapter(this, categoties,false);
                    gridView.setAdapter(categoryAdapter);
                }));
    }
}
