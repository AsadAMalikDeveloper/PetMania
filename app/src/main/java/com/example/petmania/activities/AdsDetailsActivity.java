package com.example.petmania.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.petmania.R;
import com.example.petmania.adapter.SliderAdapterExample;
import com.example.petmania.database.modeldb.Favourite;
import com.example.petmania.eventbus.MenuItemBack;
import com.example.petmania.fragments.HomeFragment;
import com.example.petmania.model.Addresses;
import com.example.petmania.model.Adds_images;
import com.example.petmania.model.User;
import com.example.petmania.retrofit.IPetManiaAPI;
import com.example.petmania.utils.Common;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import dmax.dialog.SpotsDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdsDetailsActivity extends AppCompatActivity {

    Toolbar toolbar;
    LinearLayout chatBtn,smsBtn,callBtn,noNumChat;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    AlertDialog dialog;
    private TextView detailsTv,titleTv,priceTv,locationTv,dateTv,username,userSince;
    private IPetManiaAPI mServices;
    LinearLayout showNo,noShowNo;
    int user_id =-1;
    String usernameIntent;

    @Override
    protected void onStart() {
        super.onStart();
        mServices.getUserByUserId(String.valueOf(Common.SelectedAdd.getUser_id()))
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        User user = response.body();
                        if (TextUtils.isEmpty(user.getError_msg())){
                            Common.SelectedAddUser = user;
                            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM ,yyyy");
                            Date date = new Date(Long.parseLong(user.getDate_of_join()));
                            username.setText(new StringBuilder(user.getName()));
                            userSince.setText(new StringBuilder("User since ").append(sdf.format(date)));
                            user_id = user.getUser_id();
                            usernameIntent = user.getName();
                        }else{
                            Toast.makeText(AdsDetailsActivity.this, ""+user.getError_msg(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Toast.makeText(AdsDetailsActivity.this, "[THROWABLE] "+t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads_details);
        initViews();

        compositeDisposable.add(mServices.getImages(Common.SelectedAdd.getAdds_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<ArrayList<Adds_images>>() {
                    @Override
                    public void accept(ArrayList<Adds_images> adds_images) throws Exception {
                        showImages(adds_images);
                    }
                }));

        compositeDisposable.add(mServices.loadAddressWithAddId(Common.SelectedAdd.getAddress_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(addresses -> {
                    locationTv.setText(new StringBuilder(addresses.get(0).getCity()).append(", ").append(addresses.get(0).getCountry()));
                }));



        dateTv.setText(new StringBuilder(Common.SelectedAdd.getAdd_on()));
        detailsTv.setText(new StringBuilder(Common.SelectedAdd.getAdd_detail()));
        titleTv.setText(new StringBuilder(Common.SelectedAdd.getAdd_title().toUpperCase()));
        priceTv.setText(new StringBuilder(Common.SelectedAdd.getAdd_price()).append(" Rs."));



        if (Common.SelectedAdd.getShow_no()==1){
            showNo.setVisibility(View.VISIBLE);
            noShowNo.setVisibility(View.GONE);

            chatBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Common.SelectedAddUser!=null){
                        gotoMessages();
                    }
                }
            });
            smsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Common.SelectedAddUser!=null){
                        Log.d("AdsDetailActivity", "@@@### "+Common.SelectedAddUser.getPhone());
                        Uri sms_uri = Uri.parse("smsto:"+Common.SelectedAddUser.getPhone());
                        Intent sms_intent = new Intent(Intent.ACTION_SENDTO, sms_uri);
                        sms_intent.putExtra("sms_body", "Hello Sir, "+Common.SelectedAddUser.getName()+
                                " is "+Common.SelectedAdd.getAdd_title().toUpperCase()+" available ?");
                        startActivity(sms_intent);
                    }else{
                        Toast.makeText(AdsDetailsActivity.this, "something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            callBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Common.SelectedAddUser!=null) {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", Common.SelectedAddUser.getPhone(), null));
                        startActivity(intent);
                    }else{
                        Toast.makeText(AdsDetailsActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else{
            showNo.setVisibility(View.GONE);
            noShowNo.setVisibility(View.VISIBLE);
            noNumChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gotoMessages();
                }
            });
        }
    }

    private void gotoMessages() {
        Intent intent = new Intent(AdsDetailsActivity.this,MessageActivity.class);
        intent.putExtra("user_id",user_id);
        intent.putExtra("ad_id",Common.SelectedAdd.getAdds_id());
        startActivity(intent);
    }


    private void showImages(ArrayList<Adds_images> adds_images) {
        dialog.dismiss();
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

    private void initViews() {

        toolbar = findViewById(R.id.header_text);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(Common.SelectedAdd.getAdd_title().toUpperCase());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setTitleTextColor(Color.WHITE);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_white_black_24dp);

        mServices = Common.getAPI();
        chatBtn = findViewById(R.id.chat_btn);

        smsBtn = findViewById(R.id.sms_btn);
        callBtn = findViewById(R.id.call_btn);
        noNumChat = findViewById(R.id.chat_btn1);
        showNo = findViewById(R.id.footer_showno);
        noShowNo = findViewById(R.id.footer_Noshowno);
        dialog=new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        dialog.setMessage("Loading Details");
        dialog.show();
        detailsTv = findViewById(R.id.detail_tv);
        titleTv = findViewById(R.id.title_tv);
        priceTv = findViewById(R.id.price_tv);
        locationTv = findViewById(R.id.location_tv);
        dateTv = findViewById(R.id.date_tv);
        username = findViewById(R.id.username);
        userSince = findViewById(R.id.usersince);
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

    @Override
    public void onDestroy() {
        EventBus.getDefault().postSticky(new MenuItemBack());
        super.onDestroy();
    }
}
