package com.example.petmania.fragments;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.petmania.R;
import com.example.petmania.activities.DoctorDetailActivity;
import com.example.petmania.activities.DoctorsMainActivity;
import com.example.petmania.activities.DoctorsSigninActivity;
import com.example.petmania.activities.MainActivity;
import com.example.petmania.activities.RegisterActivity;
import com.example.petmania.activities.VerificationEmailActivity;
import com.example.petmania.model.User;
import com.example.petmania.prevalent.Prevalent;
import com.example.petmania.retrofit.IPetManiaAPI;
import com.example.petmania.utils.Common;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.scottyab.aescrypt.AESCrypt;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import javax.annotation.Nullable;
import javax.crypto.EncryptedPrivateKeyInfo;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment {


    public SignInFragment() {
        // Required empty public constructor
    }

    private TextView dontHaveAnAccoubnt, forgetpass,docLoginIn;
    private FrameLayout parentFrameLayout;
    private EditText email, userPass;
    private ImageButton cancel;
    private Button signInbtn;
    private ProgressBar progressBar;
    private String emailcheck = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";

    public static boolean disableClosrBtn = false;
    CheckBox checkBox;

    IPetManiaAPI mServices;

    private void setLocale(String lang) {

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale =locale;
        getActivity().getBaseContext().getResources().updateConfiguration(configuration,getActivity().getBaseContext().getResources().getDisplayMetrics());

        SharedPreferences.Editor editor = getActivity().getSharedPreferences("Settings",Context.MODE_PRIVATE).edit();
        editor.putString("My_Lang",lang);
        editor.apply();
    }

    public void loadLocalte(){
        SharedPreferences preferences = getActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String lang = preferences.getString("My_Lang","");
        setLocale(lang);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        loadLocalte();
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        SharedPreferences preferences = getActivity().getSharedPreferences("checkbox", MODE_PRIVATE);
        String chechbox = preferences.getString("remember", "");
        Paper.init(getActivity());

        mServices = Common.getAPI();
        dontHaveAnAccoubnt = (TextView) view.findViewById(R.id.sign_in_signUpTV);
        parentFrameLayout = (FrameLayout) getActivity().findViewById(R.id.register_frameLayout);

        forgetpass = (TextView) view.findViewById(R.id.sign_in_forget_pass);
        email = (EditText) view.findViewById(R.id.sign_in_email);
        userPass = (EditText) view.findViewById(R.id.sign_in_pass);
        cancel = (ImageButton) view.findViewById(R.id.sign_in_cancel_btn);
        signInbtn = (Button) view.findViewById(R.id.sign_in_btn);
        progressBar = (ProgressBar) view.findViewById(R.id.sign_in_progressBar);
        docLoginIn = view.findViewById(R.id.docLogin);

        String userPassKey = Paper.book().read(Prevalent.userPassKey);
        String userEmailKey = Paper.book().read(Prevalent.userEmailkey);
        if (chechbox.equals("true")) {
            if (!TextUtils.isEmpty(userEmailKey) && !TextUtils.isEmpty(userPassKey)) {
                checkEmailandPass(userEmailKey, userPassKey);
            } else {
                Toast.makeText(getActivity(), "Please Enter Email and Password", Toast.LENGTH_SHORT).show();
            }
        } else if (chechbox.equals("false")) {
            Toast.makeText(getActivity(), "Please Sign in", Toast.LENGTH_SHORT).show();
        }
        checkBox = view.findViewById(R.id.rememberMe);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isChecked()) {
                    SharedPreferences preferences = getActivity().getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("remember", "true");
                    editor.apply();
                    Toast.makeText(getContext(), "RememberMe Checked", Toast.LENGTH_SHORT).show();
                } else if (!buttonView.isChecked()) {
                    SharedPreferences preferences = getActivity().getSharedPreferences("checkbox", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("remember", "false");
                    editor.apply();
                    Toast.makeText(getContext(), "RememberMe UnChecked", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (disableClosrBtn) {
            cancel.setVisibility(View.GONE);
        } else {
            cancel.setVisibility(View.VISIBLE);
        }

        docLoginIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), DoctorsSigninActivity.class);
                getActivity().startActivity(intent);
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadLocalte();
        dontHaveAnAccoubnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFregment(new RegisterFragment());

            }
        });
        forgetpass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterActivity.onResetPassword = true;
                setFregment(new ResetPasswordFragment());
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainIntent1();
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

                String password = "hello";
                String encryptedMsg =userPass.getText().toString();
                String encrpytPass = null;
                try {
                    encrpytPass = AESCrypt.encrypt(password, encryptedMsg);
                }catch (GeneralSecurityException e){
                    //handle error - could be due to incorrect password or tampered encryptedMsg
                }
                checkEmailandPass(email.getText().toString(), encrpytPass);
            }
        });
    }

    private void setFregment(Fragment fragment) {


        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_out_from_left);
        fragmentTransaction.replace(parentFrameLayout.getId(), fragment);
        fragmentTransaction.commit();
    }

    private void checkEmailandPass(String email, final String pass) {

        if (email.matches(emailcheck)) {
            if (pass.length() >= 8) {

                progressBar.setVisibility(View.VISIBLE);

                signInbtn.setEnabled(false);
                signInbtn.setTextColor(Color.argb(50, 255, 255, 255));


                mServices.signinInWithEmail(email)
                        .enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Call<User> call, Response<User> response) {
                                User user = response.body();
                                if (TextUtils.isEmpty(user.getError_msg())) {
                                    if (pass.equals(user.getPassword())) {
                                        Paper.book().write(Prevalent.userPassKey, pass);
                                        Paper.book().write(Prevalent.userEmailkey, email);
                                        Common.currentUser = user;
                                        Log.d("SignInFragment ", "onResponse: WWW333 yalo " + Common.currentUser.getVerified());
                                        mainIntent();
                                    } else {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        signInbtn.setEnabled(true);
                                        signInbtn.setTextColor(getResources().getColor(R.color.colorAccent));
                                        Toast.makeText(getActivity(), "Wrong Password ", Toast.LENGTH_LONG).show();
                                    }
                                } else {

                                    progressBar.setVisibility(View.INVISIBLE);
                                    signInbtn.setEnabled(true);
                                    signInbtn.setTextColor(getResources().getColor(R.color.colorAccent));
                                    Toast.makeText(getActivity(), "" + user.getError_msg(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<User> call, Throwable t) {
                                progressBar.setVisibility(View.INVISIBLE);
                                signInbtn.setEnabled(true);
                                signInbtn.setTextColor(getResources().getColor(R.color.colorAccent));

                                Toast.makeText(getActivity(), "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

            } else {

                Toast.makeText(getActivity(), "Incorrect email or pass", Toast.LENGTH_SHORT).show();
            }
        } else {

            Toast.makeText(getActivity(), "Incorrect email or pass", Toast.LENGTH_SHORT).show();
        }

    }

    private void mainIntent() {
        if (disableClosrBtn) {
            disableClosrBtn = false;
        } else {
            Intent main = new Intent(getActivity(), VerificationEmailActivity.class);
            startActivity(main);
        }
        getActivity().finish();
    }

    private void mainIntent1() {
        if (disableClosrBtn) {
            disableClosrBtn = false;
        } else {
            Intent main = new Intent(getActivity(), MainActivity.class);
            startActivity(main);
        }
        getActivity().finish();
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
