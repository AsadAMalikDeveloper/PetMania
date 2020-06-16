package com.example.petmania.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.petmania.R;
import com.example.petmania.model.Review;
import com.example.petmania.retrofit.IPetManiaAPI;
import com.example.petmania.utils.Common;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WriteReviewActivity extends AppCompatActivity {

    private ImageView backBtn;
    private TextView drName;
    private RatingBar ratingBar;
    private EditText reviewEt;
    private FloatingActionButton submitBtn;

    private int dr_id;
    private String dr_name;
    private float finalRating;
    private IPetManiaAPI mServices;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);
        mServices = Common.getAPI();
        dr_id = getIntent().getIntExtra("dr_id",-1);
        dr_name = getIntent().getStringExtra("dr_name");

        backBtn = findViewById(R.id.back_btn_dr);
        drName= findViewById(R.id.dr_name_tv);
        ratingBar = findViewById(R.id.ratingBar_review);
        reviewEt = findViewById(R.id.reviewEt);
        submitBtn = findViewById(R.id.submitBtn);

        drName.setText(dr_name);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float frating, boolean fromUser) {
               int rating = (int) frating;
               String message =  null;
               switch (rating){
                   case 1:
                       message = "Sorry You're really upset with us!";
                       break;
                   case 2:
                       message ="Sorry You're not happy!";
                       break;
                   case 3:
                       message = "Good enough is not good enough!";
                       break;
                   case 4:
                       message ="Thanks! We're glad you liked it !";
                       break;
                   case 5:
                       message ="Awesome! Thanks ☺!";
                       break;
               }
               finalRating= frating;
                Toast.makeText(WriteReviewActivity.this, ""+message+" "+finalRating, Toast.LENGTH_SHORT).show();
            }
        });
        checkReview();
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitData();
            }
        });
    }

    private void checkReview() {
        mServices.getReviewByUserId(Common.currentUser.getUser_id(),dr_id)
                .enqueue(new Callback<Review>() {
                    @Override
                    public void onResponse(Call<Review> call, Response<Review> response) {
                        Review review = response.body();
                        if (TextUtils.isEmpty(review.getError_msg())){
                            ratingBar.setRating(Float.parseFloat(review.getRating()));
                            reviewEt.setText(review.getReview());
                        }
                    }

                    @Override
                    public void onFailure(Call<Review> call, Throwable t) {

                    }
                });
    }

    private void submitData() {
        String review = reviewEt.getText().toString().trim();
        String timestamp = String.valueOf(System.currentTimeMillis());
        if (TextUtils.isEmpty(review)){
            Toast.makeText(this, "Please write review", Toast.LENGTH_SHORT).show();
            return;
        }

        mServices.addReview(Common.currentUser.getUser_id(),dr_id,review,String.valueOf(finalRating),timestamp).enqueue(new Callback<Review>() {
            @Override
            public void onResponse(Call<Review> call, Response<Review> response) {
                Review review1 = response.body();
                if (TextUtils.isEmpty(review1.getError_msg())){
                    Toast.makeText(WriteReviewActivity.this, "Added Successfully", Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    mServices.updateReview(Common.currentUser.getUser_id(),dr_id,String.valueOf(finalRating),review,timestamp)
                            .enqueue(new Callback<Review>() {
                                @Override
                                public void onResponse(Call<Review> call, Response<Review> response) {
                                    Review review2 = response.body();
                                    if (TextUtils.isEmpty(review2.getError_msg())){
                                        Toast.makeText(WriteReviewActivity.this, "Updated Successfully", Toast.LENGTH_SHORT).show();
                                    }else {
                                        Toast.makeText(WriteReviewActivity.this, "not updated "+review2.getError_msg(), Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onFailure(Call<Review> call, Throwable t) {
                                    Toast.makeText(WriteReviewActivity.this, "INNER FAIL "+t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }

            @Override
            public void onFailure(Call<Review> call, Throwable t) {
                Toast.makeText(WriteReviewActivity.this, "OUTER FAIL "+t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}