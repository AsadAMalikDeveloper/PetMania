package com.example.petmania.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.petmania.R;
import com.example.petmania.adapter.DoctorAdapter;
import com.example.petmania.adapter.ViewPagerAdapter;
import com.example.petmania.model.Doctors;
import com.example.petmania.utils.Common;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class DoctorDetailActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private int dr_id,br_id;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private ViewPagerAdapter adapter;
    private int pos,searchPos;
    private TextView count;
    private LottieAnimationView forward,rewind;
    private boolean isSearch;
    private ArrayList<Doctors> searchList,list;
    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_detail);

        viewPager = findViewById(R.id.view_pager);
        count = findViewById(R.id.count_tv);
        forward= findViewById(R.id.forward);
        rewind = findViewById(R.id.rewind);
        isSearch = getIntent().getBooleanExtra("isSearch",false);
        searchList = (ArrayList<Doctors>) getIntent().getSerializableExtra("SEARCHLIST");
        list = (ArrayList<Doctors>) getIntent().getSerializableExtra("LIST");
        searchPos = getIntent().getIntExtra("searchPos",-1);
        pos = getIntent().getIntExtra("pos",-1);
        if (isSearch){
            getDoctorBySearch();
        }else {
            getDoctor();
        }

    }

    private void getDoctorBySearch() {
        adapter= new ViewPagerAdapter(this,searchList);
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                viewPager.setCurrentItem(searchPos);
            }
        });
        if (searchList.size()>1){
            viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    position = position+1;
                    if (position==1){
                        rewind.setVisibility(View.GONE);
                        forward.setVisibility(View.VISIBLE);
                    }else if (position==searchList.size()){
                        rewind.setVisibility(View.VISIBLE);
                        forward.setVisibility(View.GONE);
                    }else {
                        rewind.setVisibility(View.VISIBLE);
                        forward.setVisibility(View.VISIBLE);
                    }
                    count.setText(new StringBuilder(""+(position )).append("/").append(searchList.size()));
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
        }else {
            count.setText(new StringBuilder(""+(1)).append("/").append(searchList.size()));
        }
        viewPager.setPageTransformer(true,new ViewPageStack());
        viewPager.setOffscreenPageLimit(searchList.size());
        viewPager.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    private void getDoctor() {

                    adapter= new ViewPagerAdapter(this,list);
                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            viewPager.setCurrentItem(pos);
                        }
                    });
                    if (list.size()>1){
                        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                            @Override
                            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                            }

                            @Override
                            public void onPageSelected(int position) {
                                position = position+1;
                                if (position==1){
                                    rewind.setVisibility(View.GONE);
                                    forward.setVisibility(View.VISIBLE);
                                }else if (position==list.size()){
                                    rewind.setVisibility(View.VISIBLE);
                                    forward.setVisibility(View.GONE);
                                }else {
                                    rewind.setVisibility(View.VISIBLE);
                                    forward.setVisibility(View.VISIBLE);
                                }
                                count.setText(new StringBuilder(""+(position )).append("/").append(list.size()));
                            }

                            @Override
                            public void onPageScrollStateChanged(int state) {

                            }
                        });
                    }else {
                        count.setText(new StringBuilder(""+(1)).append("/").append(list.size()));
                    }
                    viewPager.setPageTransformer(true,new ViewPageStack());
                    viewPager.setOffscreenPageLimit(list.size());
                    viewPager.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
    }

    private class  ViewPageStack implements ViewPager.PageTransformer{

        @Override
        public void transformPage(@NonNull View page, float position) {
            if (position>=0){
                page.setScaleX(0.7f - 0.05f * position);
                page.setScaleY(0.7f);

                page.setTranslationX(-page.getWidth() *position);
                page.setTranslationY(-30 * position);
            }
        }
    }
}
