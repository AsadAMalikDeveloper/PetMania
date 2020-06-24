package com.example.petmania.activities.doctorapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.petmania.R;
import com.example.petmania.model.Doctors;
import com.example.petmania.prevalent.Prevalent;
import com.example.petmania.retrofit.IPetManiaAPI;
import com.example.petmania.utils.Common;

import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DoctorsSigninActivity extends AppCompatActivity {

    private TextView forgetpass;
    private EditText email, userPass;
    private Button signInbtn;
    private ProgressBar progressBar;
    private String emailcheck = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";

    IPetManiaAPI mServices;
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
        SharedPreferences preferences = getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String lang = preferences.getString("My_Lang","");
        setLocale(lang);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mServices = Common.getAPI();

        setContentView(R.layout.activity_doctors_signin);
        forgetpass = (TextView) findViewById(R.id.sign_in_forget_pass_dr);
        email = (EditText) findViewById(R.id.sign_in_email_dr);
        userPass = (EditText) findViewById(R.id.sign_in_pass_dr);
        signInbtn = (Button) findViewById(R.id.sign_in_btn_dr);
        progressBar = (ProgressBar) findViewById(R.id.sign_in_progressBar_dr);
        forgetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        userPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        signInbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                checkEmailandPass(email.getText().toString(), userPass.getText().toString());
            }
        });
    }
    private void checkEmailandPass(String email, final String pass) {

        if (email.matches(emailcheck)) {
            if (pass.length() >= 8) {

                progressBar.setVisibility(View.VISIBLE);

                signInbtn.setEnabled(false);
                signInbtn.setTextColor(Color.argb(50, 255, 255, 255));


                mServices.signinInWithEmailDr(email)
                        .enqueue(new Callback<Doctors>() {
                            @Override
                            public void onResponse(Call<Doctors> doctors, Response<Doctors> response) {
                                Doctors doctor = response.body();
                                if (TextUtils.isEmpty(doctor.getError_msg())) {
                                    if (pass.equals(doctor.getDr_pass())) {
                                        Paper.book().write(Prevalent.drEmailKey, email);
                                        Paper.book().write(Prevalent.drPassKey, pass);
                                        Common.currentDoctor = doctor;
                                        mainIntent();
                                    } else {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        signInbtn.setEnabled(true);
                                        signInbtn.setTextColor(getResources().getColor(R.color.colorAccent));
                                        Toast.makeText(DoctorsSigninActivity.this, "Wrong Password ", Toast.LENGTH_LONG).show();
                                    }
                                } else {

                                    progressBar.setVisibility(View.INVISIBLE);
                                    signInbtn.setEnabled(true);
                                    signInbtn.setTextColor(getResources().getColor(R.color.colorAccent));
                                    Toast.makeText(DoctorsSigninActivity.this, "" +doctor.getError_msg(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<Doctors> call, Throwable t) {
                                progressBar.setVisibility(View.INVISIBLE);
                                signInbtn.setEnabled(true);
                                signInbtn.setTextColor(getResources().getColor(R.color.colorAccent));

                                Toast.makeText(DoctorsSigninActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

            } else {

                Toast.makeText(DoctorsSigninActivity.this, "Incorrect email or pass", Toast.LENGTH_SHORT).show();
            }
        } else {

            Toast.makeText(DoctorsSigninActivity.this, "Incorrect email or pass", Toast.LENGTH_SHORT).show();
        }

    }

    private void mainIntent() {
            Intent main = new Intent(DoctorsSigninActivity.this, DoctorsMainActivity.class);
            startActivity(main);
            finish();
    }

    private void checkInputs() {
        if (!TextUtils.isEmpty(email.getText())) {
            if (!TextUtils.isEmpty(userPass.getText())) {

                signInbtn.setEnabled(true);
                signInbtn.setTextColor(getResources().getColor(R.color.colorAccent));

            } else {
                signInbtn.setEnabled(false);
                signInbtn.setTextColor(Color.argb(50, 255, 255, 255));
            }
        } else {
            signInbtn.setEnabled(false);
            signInbtn.setTextColor(Color.argb(50, 255, 255, 255));

        }
    }
}
