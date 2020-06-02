package com.example.petmania.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.petmania.R;
import com.example.petmania.model.User;
import com.example.petmania.retrofit.IPetManiaAPI;
import com.example.petmania.utils.Common;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerificationEmailActivity extends AppCompatActivity {

    @Override
    protected void onStart() {
        super.onStart();
        if (Common.currentUser!=null){
            if(Common.currentUser.getVerified()!=0){
                gotoMain();
            }
        }
    }

    private void gotoMain(){
        Intent intent = new Intent(VerificationEmailActivity.this,MainActivity.class);
        startActivity(intent);finish();
    }

    private EditText codeText;
    IPetManiaAPI mServices;
    @SuppressLint("LongLogTag")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_email);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mServices = Common.getAPI();


        codeText = findViewById(R.id.code_txt);

    }

    public void submitCode(View view) {
        if (codeText.getText().toString().equals(Common.currentUser.getVkey())){
            mServices.updateVkey(Common.currentUser.getEmail(),1).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    User user = response.body();
                    Common.currentUser = user;
                    Toast.makeText(VerificationEmailActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(VerificationEmailActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {

                }
            });
        }else{
            Toast.makeText(this, "Invalid Code", Toast.LENGTH_SHORT).show();
        }
    }
}
