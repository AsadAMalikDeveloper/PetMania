package com.example.petmania.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.petmania.R;
import com.example.petmania.fragments.AccountFragment;
import com.example.petmania.fragments.AddsFragment;
import com.example.petmania.fragments.ChatMainFragment;
import com.example.petmania.fragments.ClinicFragment;
import com.example.petmania.fragments.FavouriteFragment;
import com.example.petmania.fragments.HomeFragment;
import com.example.petmania.fragments.RegisterFragment;
import com.example.petmania.fragments.SettingFragment;
import com.example.petmania.fragments.SignInFragment;
import com.example.petmania.utils.Common;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.auth.Token;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.luseen.spacenavigation.SpaceItem;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.luseen.spacenavigation.SpaceOnClickListener;

import java.util.Locale;

import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {


    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private NavigationView nvDrawer;
    ImageView actionBarLogo;
    TextView username;
    TextView userMail;
    TextView texttoolbar;
    // Make sure to be using androidx.appcompat.app.ActionBarDrawerToggle version.
    private ActionBarDrawerToggle drawerToggle;
    private Dialog signInDialog;
    private static int fragNo = -2;
    private static int currentFragment = -1;

    FrameLayout frameLayout;

    SpaceNavigationView spaceNavigationView;
    RelativeLayout navLayout;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        actionBarLogo = findViewById(R.id.action_bar_logo);
        texttoolbar = findViewById(R.id.toolbarText);
        setSupportActionBar(toolbar);
        Paper.init(this);
        navLayout = findViewById(R.id.navLayout);
        frameLayout = findViewById(R.id.flContent);
        nvDrawer = (NavigationView) findViewById(R.id.nav_view);
        // Find our drawer view
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerToggle = setupDrawerToggle();
        // Setup toggle to display hamburger icon with nice animation
        drawerToggle.setDrawerIndicatorEnabled(true);
        drawerToggle.syncState();

        // Tie DrawerLayout events to the ActionBarToggle
        mDrawer.addDrawerListener(drawerToggle);
        // Setup drawer view
        setupDrawerContent(nvDrawer);
        View headerView = nvDrawer.getHeaderView(0);


        setFragment(new HomeFragment(), 0);
        userMail = headerView.findViewById(R.id.main_email);
        username = headerView.findViewById(R.id.main_fullname);
        initSignInDialog();

        spaceNavigationView = findViewById(R.id.space);
        spaceNavigationView.initWithSaveInstanceState(savedInstanceState);
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.ic_home));
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.ads));
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.inbox));
        spaceNavigationView.addSpaceItem(new SpaceItem("", R.drawable.ic_setting));
        spaceNavigationView.setCentreButtonIconColorFilterEnabled(true);

        spaceNavigationView.setSpaceOnClickListener(new SpaceOnClickListener() {
            @Override
            public void onCentreButtonClick() {
                Intent intent = new Intent(MainActivity.this,NewFeedsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                spaceNavigationView.setCentreButtonSelectable(true);
            }

            @Override
            public void onItemClick(int itemIndex, String itemName) {
                if (Common.currentUser != null) {
                    switch (itemIndex) {
                        case 0:
                            gotoFragment("Home", new HomeFragment(), 0);
                            break;
                        case 1:
                            gotoFragment("My Ads", new AddsFragment(), 2);
                            break;
                        case 2:
                            Intent intent = new Intent(MainActivity.this,ChatMainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            break;
                        case 3:
                            /*Intent intent = new Intent(MainActivity.this,CheckActivity.class);
                            intent.addFlags(
                                Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);*/
                            gotoFragment("Settings", new SettingFragment(), 3);
                            break;
                    }
                } else {
                    signInDialog.show();
                }
            }

            @Override
            public void onItemReselected(int itemIndex, String itemName) {
                if (Common.currentUser != null) {
                    switch (itemIndex) {
                        case 0:
                            gotoFragment("Home", new HomeFragment(), 0);
                            break;
                        case 1:
                            gotoFragment("My Ads", new AddsFragment(), 2);
                            break;
                        case 2:
                            Intent intent = new Intent(MainActivity.this,ChatMainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            break;
                        case 3:
                            gotoFragment("Settings", new SettingFragment(), 3);
                            /*Intent intent = new Intent(MainActivity.this,CheckActivity.class);
                            intent.addFlags(
                                    Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);*/
                            break;
                    }
                } else {
                    signInDialog.show();
                }
            }
        });

        FirebaseInstanceId.getInstance()
                .getInstanceId().addOnFailureListener(e -> {
            Toast.makeText(this, "fail", Toast.LENGTH_SHORT).show();
        }).addOnCompleteListener(task -> {
            Common.updateToken(MainActivity.this,task.getResult().getToken());
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        spaceNavigationView.onSaveInstanceState(outState);
    }


    private void initSignInDialog() {
        signInDialog = new Dialog(MainActivity.this);
        signInDialog.setContentView(R.layout.sign_in_dialog);
        signInDialog.setCancelable(true);
        signInDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Button dialogSignInBtn = signInDialog.findViewById(R.id.sign_in_btn);
        Button dialogSignUpBtn = signInDialog.findViewById(R.id.sign_up_btn);
        final Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);

        dialogSignInBtn.setOnClickListener(v -> {
            SignInFragment.disableClosrBtn = true;
            RegisterFragment.disableCloseBtn = true;
            signInDialog.dismiss();
            RegisterActivity.setSignUpFragment = false;
            startActivity(registerIntent);
        });

        dialogSignUpBtn.setOnClickListener(v -> {
            RegisterFragment.disableCloseBtn = true;
            SignInFragment.disableClosrBtn = true;
            signInDialog.dismiss();
            RegisterActivity.setSignUpFragment = true;
            startActivity(registerIntent);
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
                actionBarLogo.setVisibility(View.VISIBLE);
                texttoolbar.setVisibility(View.VISIBLE);
                invalidateOptionsMenu();
                setFragment(new HomeFragment(), 0);
                nvDrawer.getMenu().getItem(0).setChecked(true);
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.main_search_icon) {
            return true;
        } else if (id == R.id.main_noti_icon) {
            if (Common.currentUser == null) {
                signInDialog.show();
            } else {
                Toast.makeText(this, "TODO", Toast.LENGTH_SHORT).show();
            }
        } else if (id == android.R.id.home) {
            finish();
            mDrawer.openDrawer(GravityCompat.START);
            return true;
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (currentFragment == 0) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getMenuInflater().inflate(R.menu.main, menu);
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadLocalte();
        if (Common.currentUser == null) {
            nvDrawer.getMenu().getItem(nvDrawer.getMenu().size() - 1).setVisible(false);
        } else {

            userMail.setText(Common.currentUser.getEmail());
            username.setText(Common.currentUser.getName());

            nvDrawer.getMenu().getItem(nvDrawer.getMenu().size() - 1).setVisible(true);
        }
    }



    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    if (Common.currentUser != null) {
                        selectDrawerItem(menuItem);
                        return true;
                    } else {
                        signInDialog.show();
                        return false;
                    }
                });
    }

    public void selectDrawerItem(MenuItem menuItem) {
        // Create a new fragment and specify the fragment to show based on nav item clicked
        Fragment fragment = null;
        Class fragmentClass;

        switch (menuItem.getItemId()) {
            case R.id.nav_home:
                Common.bottomNavigationView = spaceNavigationView;
                actionBarLogo.setVisibility(View.VISIBLE);
                invalidateOptionsMenu();
                fragmentClass = HomeFragment.class;
                fragNo = 0;
                break;
            case R.id.nav_myaccount:
                Common.bottomNavigationView = spaceNavigationView;
                fragmentClass = AccountFragment.class;
                fragNo = 1;
                break;
            case R.id.nav_fav:
                Common.bottomNavigationView = spaceNavigationView;
                fragmentClass = FavouriteFragment.class;
                fragNo = 4;
                break;
            case R.id.nav_doc:
                Common.bottomNavigationView = spaceNavigationView;
                fragmentClass = ClinicFragment.class;
                fragNo = 6;
                break;
            case R.id.nav_changeLang:
                showChangeDialog();
                fragmentClass = HomeFragment.class;
                fragNo = 0;
                break;
            case R.id.nav_signout:
                signout();
            default:
                fragmentClass = HomeFragment.class;
        }
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Insert the fragment by replacing any existing fragment


        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        // Set action bar title
        gotoFragment(menuItem.getTitle().toString(), fragment, fragNo);
        // Close the navigation drawer
        mDrawer.closeDrawers();
    }

    private void signout() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
        builder1.setMessage("Logout From PetMania ?");
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SharedPreferences preferences = getSharedPreferences("checkbox", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("remember", "false");
                        editor.apply();
                        Paper.book().destroy();
                        Common.currentUser = null;
                        setFragment(new SignInFragment(), 2);
                        MainActivity.this.finish();
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
    private void showChangeDialog() {
        final String [] listsItems = {"English","Urdu"};
        androidx.appcompat.app.AlertDialog.Builder mBuilder = new androidx.appcompat.app.AlertDialog.Builder(this);
        mBuilder.setTitle("Choose Language");
        mBuilder.setSingleChoiceItems(listsItems, -1, (dialog, i) -> {
            if (i==0){
                setLocale("en");
                recreate();
            }else if (i==1){
                setLocale("ur");
                recreate();
            }
            dialog.dismiss();
        });

        androidx.appcompat.app.AlertDialog dialog= mBuilder.create();
        dialog.show();
    }

    private void setLocale(String lang) {

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale =locale;
        getBaseContext().getResources().updateConfiguration(configuration,getBaseContext().getResources().getDisplayMetrics());

        SharedPreferences.Editor editor = getSharedPreferences("Settings", Context.MODE_PRIVATE).edit();
        editor.putString("My_Lang",lang);
        editor.apply();
    }
    public void loadLocalte(){
        SharedPreferences preferences = getSharedPreferences("Settings",Context.MODE_PRIVATE);
        String lang = preferences.getString("My_Lang","");
        setLocale(lang);
    }

    private void gotoFragment(String title, Fragment fragment, int fragmentNo) {
        if (fragmentNo != 0) { //HomeFragment

            navLayout.setVisibility(View.VISIBLE);
            texttoolbar.setVisibility(View.GONE);
            actionBarLogo.setVisibility(View.GONE);
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            getSupportActionBar().setTitle(title);
            invalidateOptionsMenu();
            setFragment(fragment, fragmentNo);
        } else if (fragmentNo == 1) {
            navLayout.setVisibility(View.INVISIBLE);
            texttoolbar.setVisibility(View.VISIBLE);
            actionBarLogo.setVisibility(View.VISIBLE);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            invalidateOptionsMenu();
            setFragment(fragment, fragmentNo);
        } else {
            navLayout.setVisibility(View.VISIBLE);
            texttoolbar.setVisibility(View.VISIBLE);
            actionBarLogo.setVisibility(View.VISIBLE);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            invalidateOptionsMenu();
            setFragment(fragment, fragmentNo);
        }
    }

    private void setFragment(Fragment fragment, int framentNo) {
        if (framentNo != currentFragment) {

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
