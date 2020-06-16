package com.example.petmania.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.petmania.R;
import com.example.petmania.adapter.ShowReviewAdapter;
import com.example.petmania.model.CheckUserResponse;
import com.example.petmania.model.Review;
import com.example.petmania.utils.Common;

import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowReviewActivity extends AppCompatActivity {

    private TextView drName;
    private RatingBar ratingBar;
    private RecyclerView reviewRecycler;
    private ArrayList<Review> reviewArrayList;
    private ShowReviewAdapter adapter;
    private int drID;
    private String dr_name;
    private TextView rartingCount;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private float finalRating;
    private float ratingSum = 0;
    private int countReview =0;
    private float avgRating = 0;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_review);
        drID = getIntent().getIntExtra("dr_id",-1);
        dr_name = getIntent().getStringExtra("dr_name");

        drName = findViewById(R.id.dr_name_tv_review);
        ratingBar = findViewById(R.id.ratingBar_review);
        rartingCount = findViewById(R.id.rating_count);
        reviewRecycler = findViewById(R.id.recycler_review);
        reviewRecycler.setLayoutManager(new LinearLayoutManager(this));
        reviewRecycler.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        reviewArrayList = new ArrayList<>();

        drName.setText(dr_name);
        Common.getAPI().checkDrReview(drID)
                .enqueue(new Callback<CheckUserResponse>() {
                    @Override
                    public void onResponse(Call<CheckUserResponse> call, Response<CheckUserResponse> response) {
                        CheckUserResponse response1 = response.body();
                        if (response1.isExists()){
                            compositeDisposable.add(Common.getAPI().getAllReviewDr(drID)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(reviews ->{
                                        for (Review review:reviews){
                                            float rating = Float.parseFloat(review.getRating());
                                            ratingSum = ratingSum+rating;
                                            countReview = reviews.size();
                                            avgRating = ratingSum/countReview;
                                            ratingBar.setRating(avgRating);
                                            rartingCount.setText(new StringBuilder(String.format("%.2f",avgRating)).append("["+countReview+"]"));
                                        }
                                            adapter = new ShowReviewAdapter(reviews,ShowReviewActivity.this);
                                            reviewRecycler.setAdapter(adapter);
                                            adapter.notifyDataSetChanged();


                                    }));
                        }else{

                        }
                    }

                    @Override
                    public void onFailure(Call<CheckUserResponse> call, Throwable t) {

                    }
                });
    }
}