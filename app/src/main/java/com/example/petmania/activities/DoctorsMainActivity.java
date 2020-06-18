package com.example.petmania.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.petmania.R;
import com.example.petmania.fragments.AccountFragment;
import com.example.petmania.fragments.ClinicFragment;
import com.example.petmania.fragments.DoctorHomeFragment;
import com.example.petmania.fragments.DoctorProfileFragment;
import com.example.petmania.fragments.DoctorSettingFragment;
import com.example.petmania.fragments.FavouriteFragment;
import com.example.petmania.fragments.HomeFragment;
import com.example.petmania.fragments.SignInFragment;
import com.example.petmania.model.CheckUserResponse;
import com.example.petmania.model.Review;
import com.example.petmania.utils.Common;
import com.firebase.ui.auth.ui.email.TroubleSigningInFragment;
import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

import io.paperdb.Paper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DoctorsMainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    static final float END_SCALE =0.7f;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView menuIcon;
    private LinearLayout contentView;
    private TextView ratingtv;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private float ratingSum=0;
    private int countReview =0;
    private float avgRating = 0;

    @Override
    protected void onStop() {
        super.onStop();
        compositeDisposable.dispose();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_doctors_main);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        menuIcon = findViewById(R.id.menu_icon);
        contentView = findViewById(R.id.content);
        View headerView = navigationView.getHeaderView(0);
        ratingtv =headerView.findViewById(R.id.main_rating_dr);
        setRating();


        navigationDrawer();

    }

    private void setRating() {
        Common.getAPI().checkDrReview(Common.currentDoctor.getId())
                .enqueue(new Callback<CheckUserResponse>() {
                    @Override
                    public void onResponse(Call<CheckUserResponse> call, Response<CheckUserResponse> response) {
                        CheckUserResponse response1 = response.body();
                        if (response1.isExists()){
                            compositeDisposable.add(Common.getAPI().getAllReviewDr(Common.currentDoctor.getId())
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(reviews ->{
                                        for (Review review:reviews){
                                            float rating = Float.parseFloat(review.getRating());
                                            ratingSum = ratingSum+rating;
                                            countReview = reviews.size();
                                            avgRating = ratingSum/countReview;
                                            ratingtv.setText(new StringBuilder("Rated: ").append(String.format("%.2f",avgRating)));
                                        }


                                    }));
                        }else{

                        }
                    }

                    @Override
                    public void onFailure(Call<CheckUserResponse> call, Throwable t) {

                    }
                });
    }

    private void navigationDrawer() {
        navigationView.setCheckedItem(R.id.dr_home);
        navigationView.getMenu().performIdentifierAction(R.id.dr_home, 0);
        navigationView.setItemIconTintList(null);
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(this);


        menuIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (drawerLayout.isDrawerVisible(GravityCompat.START))
                    drawerLayout.closeDrawer(GravityCompat.START);
                else drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        animateNavigationDrawer();
    }
    private void animateNavigationDrawer() {
        drawerLayout.setScrimColor(getResources().getColor(R.color.colorPrimary));
        drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                final float diffScaleoffSet= slideOffset * (1 - END_SCALE);
                final  float offsetScale = 1- diffScaleoffSet;
                contentView.setScaleX(offsetScale);
                contentView.setScaleY(offsetScale);

                final float xOffset =drawerView.getWidth() * slideOffset;
                final float xOffsetDiff = contentView.getWidth() * diffScaleoffSet / 2;
                final float xtranslation = xOffset - xOffsetDiff;
                contentView.setTranslationX(xtranslation);
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerVisible(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else
            super.onBackPressed();
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.dr_profile:
                Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }
}
