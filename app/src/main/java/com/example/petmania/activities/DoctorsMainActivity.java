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
import android.widget.FrameLayout;
import android.widget.ImageView;
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
import com.example.petmania.utils.Common;
import com.firebase.ui.auth.ui.email.TroubleSigningInFragment;
import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

import io.paperdb.Paper;

public class DoctorsMainActivity extends AppCompatActivity {
    private  DrawerLayout mDrawer;
    private NavigationView nvDrawer;
    private TextView titleTxt;
    private FrameLayout frameLayout;
    private static int fragNo = -2;
    private ActionBarDrawerToggle drawerToggle;
    private Toolbar toolbar;
    private ImageView actionBarImg;
    private TextView drName,drEmail,drRating;
    private static int currentFragment = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctors_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mDrawer = findViewById(R.id.drawer_layout);
        nvDrawer = findViewById(R.id.navigationView);
        titleTxt = findViewById(R.id.toolbarText_dr);
        actionBarImg = findViewById(R.id.action_bar_logo_dr);
        frameLayout = findViewById(R.id.flContent);
        nvDrawer.setItemIconTintList(null);
        drawerToggle = setupDrawerToggle();
        // Setup toggle to display hamburger icon with nice animation
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerToggle.syncState();

        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.addDrawerListener(drawerToggle);
        setupDrawerContent(nvDrawer);
        View headerView = nvDrawer.getHeaderView(0);


        gotoFragment("Home",new DoctorHomeFragment(),0);
        drName = headerView.findViewById(R.id.main_fullname_dr);
        drEmail = headerView.findViewById(R.id.main_email_dr);
        drRating = headerView.findViewById(R.id.main_rating_dr);
        drName.setText(Common.currentDoctor.getDr_name());
        drEmail.setText(Common.currentDoctor.getDr_email());
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                        selectDrawerItem(menuItem);
                        return true;

                });
    }
    private void signout() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Logout From PetMania ?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Paper.book().destroy();
                        Common.currentDoctor = null;
                        Intent intent = new Intent(DoctorsMainActivity.this,DoctorsSigninActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

        builder1.setNegativeButton(
                "No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass = null;
        menuItem.setChecked(false);
        switch (menuItem.getItemId()) {
            case R.id.dr_home:
                invalidateOptionsMenu();
                fragmentClass = DoctorHomeFragment.class;
                fragNo = 0;
                break;
            case R.id.dr_profile:
                fragmentClass = DoctorProfileFragment.class;
                fragNo = 1;
                break;
            case R.id.dr_settings:
                fragmentClass = DoctorSettingFragment.class;
                fragNo = 2;
                break;
            case R.id.dr_exit:
                signout();
                break;
            default:
                fragmentClass = DoctorHomeFragment.class;
        }
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Set action bar title
        gotoFragment(menuItem.getTitle().toString(), fragment, fragNo);
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }

    private void gotoFragment(String title, Fragment fragment, int fragmentNo) {
        if (fragmentNo != 0) { //HomeFragment

            actionBarImg.setVisibility(View.GONE);
            titleTxt.setText(title);
            invalidateOptionsMenu();
            setFragment(fragment, fragmentNo);
        } else if (fragmentNo == 1) {
            actionBarImg.setVisibility(View.GONE);
            titleTxt.setText(title);
            invalidateOptionsMenu();
            setFragment(fragment, fragmentNo);
        } else {
            actionBarImg.setVisibility(View.VISIBLE);
            titleTxt.setText(title);
            invalidateOptionsMenu();
            setFragment(fragment, fragmentNo);
        }
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (currentFragment == 0) {
                currentFragment = -1;
                super.onBackPressed();
            } else {
                invalidateOptionsMenu();
                setFragment(new HomeFragment(), 0);
                nvDrawer.getMenu().getItem(0).setChecked(true);
            }
        }
    }
    private void setFragment(Fragment fragment, int framentNo) {
        if (framentNo != currentFragment) {
            Toast.makeText(this, "f "+framentNo+" c "+currentFragment, Toast.LENGTH_SHORT).show();
            currentFragment = framentNo;
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.fade_in, R.anim.fade_out);
            fragmentTransaction.replace(frameLayout.getId(), fragment);
            fragmentTransaction.commit();
        }
    }
    private ActionBarDrawerToggle setupDrawerToggle() {
        // NOTE: Make sure you pass in a valid toolbar reference.  ActionBarDrawToggle() does not require it
        // and will not render the hamburger icon without it.
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.drawer_open, R.string.drawer_close);
    }
}
