package com.example.petmania.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.FrameLayout;

import com.example.petmania.R;
import com.example.petmania.fragments.RegisterFragment;
import com.example.petmania.fragments.SignInFragment;
import com.example.petmania.utils.Common;

public class RegisterActivity extends AppCompatActivity {

    FrameLayout frameLayout;
    public static boolean onResetPassword=false;
    public static boolean setSignUpFragment = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        frameLayout=(FrameLayout)findViewById(R.id.register_frameLayout);
        if(setSignUpFragment){
            setSignUpFragment =false;
            setDefaultFragment(new RegisterFragment());
        }else{

            setDefaultFragment(new SignInFragment());
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            RegisterFragment.disableCloseBtn =false;
            SignInFragment.disableClosrBtn = false;
            if(onResetPassword){
                onResetPassword=false;
                setFregment(new SignInFragment());
                return false;
            }
        }
        return super.onKeyDown(keyCode, event);

    }

    private void setDefaultFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(frameLayout.getId(),fragment);
        fragmentTransaction.commit();
    }

    private void setFregment(Fragment fragment) {

        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_left,R.anim.slide_out_from_right);
        fragmentTransaction.replace(frameLayout.getId(),fragment);
        fragmentTransaction.commit();
    }
}