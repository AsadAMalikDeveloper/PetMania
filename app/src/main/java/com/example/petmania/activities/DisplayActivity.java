package com.example.petmania.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.petmania.R;
import com.example.petmania.adapter.AddsAdapter;
import com.example.petmania.adapter.SliderAdapterExample;
import com.example.petmania.model.Adds;
import com.example.petmania.model.Adds_images;
import com.example.petmania.model.CheckUserResponse;
import com.example.petmania.prevalent.Prevalent;
import com.example.petmania.retrofit.IPetManiaAPI;
import com.example.petmania.utils.Common;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.paperdb.Paper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DisplayActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private IPetManiaAPI mServices;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    private TextView title,price,description,expire;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        toolbar = findViewById(R.id.header_text);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(Common.EditSelectedAd.getAdd_title().toUpperCase());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitleTextColor(Color.WHITE);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_white_black_24dp);
        mServices = Common.getAPI();
        description = findViewById(R.id.description_val);
        price = findViewById(R.id.price);
        title = findViewById(R.id.title);
        expire = findViewById(R.id.expireON_tv);

        expire.setText(new StringBuilder("This Ad will Expire on ").append(Common.EditSelectedAd.getExpire_on()));
        description.setText(new StringBuilder(Common.EditSelectedAd.getAdd_detail()));
        title.setText(new StringBuilder(Common.EditSelectedAd.getAdd_title()));
        price.setText(new StringBuilder(Common.EditSelectedAd.getAdd_price()).append(" Rs."));
        mServices.checkExistAdds(String.valueOf(Common.currentUser.getUser_id())).enqueue(new Callback<CheckUserResponse>() {
            @Override
            public void onResponse(Call<CheckUserResponse> call, Response<CheckUserResponse> response) {
                CheckUserResponse checkUserResponse = response.body();
                if (checkUserResponse.isExists()){

                    compositeDisposable.add(mServices.getImages(Common.EditSelectedAd.getAdds_id())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(adds_images -> showImages(adds_images)));
                }else{

                }
            }

            @Override
            public void onFailure(Call<CheckUserResponse> call, Throwable t) {

                Toast.makeText(DisplayActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void showImages(ArrayList<Adds_images> adds_images) {
        SliderView sliderView = findViewById(R.id.imageSlider);

        SliderAdapterExample adapter = new SliderAdapterExample(this,adds_images);

        sliderView.setSliderAdapter(adapter);

        sliderView.setIndicatorAnimation(IndicatorAnimations.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(4); //set scroll delay in seconds :
        sliderView.startAutoCycle();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
