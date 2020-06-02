package com.example.petmania.fragments;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.L;
import com.example.petmania.R;
import com.example.petmania.activities.MainActivity;
import com.example.petmania.activities.VerificationEmailActivity;
import com.example.petmania.activities.VerifyPhoneActivity;
import com.example.petmania.model.Addresses;
import com.example.petmania.model.User;
import com.example.petmania.prevalent.Prevalent;
import com.example.petmania.retrofit.IPetManiaAPI;
import com.example.petmania.utils.Common;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.scottyab.aescrypt.AESCrypt;

import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterFragment extends Fragment {

    public RegisterFragment() {
        // Required empty public constructor
    }

    private TextView changeLangBtn;
    private static final String TAG ="RegisterFragment";
    private TextView alreadyHaveAnAccoubnt;
    private FrameLayout parentFrameLayout;
    private EditText email, fullname, pass, cpass;
    private ImageButton cancel;
    private Button signUpbtn;
    private ProgressBar progressBar;
    String encryptedPass = null;


    IPetManiaAPI mServices;
    private static final int REQUEST_PERMISSION = 1001;

    private String emailcheck = "[a-zA-Z0-9._-]+@[a-z]+.[a-z]+";
    public static boolean disableCloseBtn = false;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        loadLocalte();
        View view = inflater.inflate(R.layout.fragment_register, container, false);
        mServices = Common.getAPI();
        alreadyHaveAnAccoubnt = (TextView) view.findViewById(R.id.sign_up_signINTV);
        Paper.init(getActivity());

        email = (EditText) view.findViewById(R.id.sign_up_email);
        fullname = (EditText) view.findViewById(R.id.sign_up_fullname);
        pass = (EditText) view.findViewById(R.id.sign_up_pass);
        cpass = (EditText) view.findViewById(R.id.sign_up_cpass);
        cancel = (ImageButton) view.findViewById(R.id.sign_up_cancel_btn);
        signUpbtn = (Button) view.findViewById(R.id.sign_up_btn);
        progressBar = (ProgressBar) view.findViewById(R.id.sign_up_progressBar);
        parentFrameLayout = (FrameLayout) getActivity().findViewById(R.id.register_frameLayout);
        changeLangBtn = (TextView) view.findViewById(R.id.change_lang_btn);



        if (disableCloseBtn) {
            cancel.setVisibility(View.GONE);
        } else {
            cancel.setVisibility(View.VISIBLE);
        }

//SetLocation
        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        changeLangBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeDialog();
            }
        });
        alreadyHaveAnAccoubnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setFregment(new SignInFragment());

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
        fullname.addTextChangedListener(new TextWatcher() {
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
        pass.addTextChangedListener(new TextWatcher() {
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
        cpass.addTextChangedListener(new TextWatcher() {
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
        signUpbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                checkEmailandPass();

            }
        });



    }

    private void showChangeDialog() {
        final String [] listsItems = {"English","Urdu"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        mBuilder.setTitle("Choose Language");
        mBuilder.setSingleChoiceItems(listsItems, -1, (dialog, i) -> {
            if (i==0){
                setLocale("en");
                getActivity().recreate();
            }else if (i==1){
                setLocale("ur");
                getActivity().recreate();
            }
            dialog.dismiss();
        });

        AlertDialog dialog= mBuilder.create();
        dialog.show();
    }

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
        SharedPreferences preferences = getActivity().getSharedPreferences("Settings",Context.MODE_PRIVATE);
        String lang = preferences.getString("My_Lang","");
        setLocale(lang);
    }


    private void checkEmailandPass() {

        if(email.getText().toString().matches(emailcheck)){
            if(pass.getText().toString().equals(cpass.getText().toString())){

                progressBar.setVisibility(View.VISIBLE);

                signUpbtn.setEnabled(false);
                signUpbtn.setTextColor(Color.argb(50,255,255,255));

                try {
                    encryptedPass = AESCrypt.encrypt("hello", pass.getText().toString());
                }catch (GeneralSecurityException e){
                    //handle error
                    Toast.makeText(getContext(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                mServices.registerNewUser(email.getText().toString(),encryptedPass,fullname.getText().toString(),"",""
                ,String.valueOf(System.currentTimeMillis()))
                        .enqueue(new Callback<User>() {
                            @Override
                            public void onResponse(Call<User> call, Response<User> response) {
                                Log.d(TAG, "onResponse: WWW123 response "+response.body());
                                progressBar.setVisibility(View.INVISIBLE);
                                User user =response.body();
                                if (TextUtils.isEmpty(user.getError_msg())){

                                    HashMap<Object,String> userMap = new HashMap<>();
                                    userMap.put("uid", String.valueOf(user.getUser_id()));
                                    userMap.put("onlineStatus","online");
                                    userMap.put("typingTo","noOne");
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference reference = database.getReference("Users");
                                    reference.child(String.valueOf(Common.currentUser.getUser_id())).setValue(userMap);

                                    Common.currentUser = response.body();
                                    Log.d(TAG, "onResponse: WWW123 true "+response.body());
                                    Toast.makeText(getContext(), "User Register Successfully", Toast.LENGTH_SHORT).show();
                                    Paper.book().write(Prevalent.userPassKey,Common.currentUser.getPassword());
                                    Paper.book().write(Prevalent.userEmailkey,email.getText().toString());
                                    mainIntent();
                                }else{

                                    Log.d(TAG, "onResponse: WWW123 responselse "+response.body());
                                    progressBar.setVisibility(View.INVISIBLE);
                                    signUpbtn.setEnabled(true);
                                    signUpbtn.setTextColor(getResources().getColor(R.color.colorAccent));
                                    Toast.makeText(getContext(),"Response Error "+ user.getError_msg(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<User> call, Throwable t) {

                                Log.d(TAG, "onResponse: WWW123 responsefal "+t.getMessage());
                                progressBar.setVisibility(View.INVISIBLE);
                                signUpbtn.setEnabled(true);
                                signUpbtn.setTextColor(getResources().getColor(R.color.colorAccent));
                                Toast.makeText(getContext(), "Register failure "+t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });


            }else{

                cpass.setError("Password Doesn't Matched");
            }
        }else{

            email.setError("Email Invalid");
        }

    }

    private void mainIntent() {
        if (disableCloseBtn){
            disableCloseBtn= false;
        }else{
            Intent main=new Intent(getActivity(), VerificationEmailActivity.class);
            startActivity(main);
        }
        getActivity().finish();
    }

    private void mainIntent1() {
        if (disableCloseBtn){
            disableCloseBtn= false;
        }else{
            Intent main=new Intent(getActivity(), MainActivity.class);
            startActivity(main);
        }
        getActivity().finish();
    }

    private void checkInputs() {
        if(!TextUtils.isEmpty(email.getText())){
            if(!TextUtils.isEmpty(fullname.getText())){
                if(!TextUtils.isEmpty(pass.getText()) && pass.length()>=8){
                    if(!TextUtils.isEmpty(cpass.getText())){
                        signUpbtn.setEnabled(true);
                        signUpbtn.setTextColor(getResources().getColor(R.color.colorAccent));
                    }else{

                        signUpbtn.setEnabled(false);
                        signUpbtn.setTextColor(Color.argb(50,255,255,255));
                    }
                }else{

                    signUpbtn.setEnabled(false);
                    signUpbtn.setTextColor(Color.argb(50,255,255,255));
                }
            }else{

                signUpbtn.setEnabled(false);
                signUpbtn.setTextColor(Color.argb(50,255,255,255));
            }
        }else{
            signUpbtn.setEnabled(false);
            signUpbtn.setTextColor(Color.argb(50,255,255,255));
        }
    }

    private void setFregment(Fragment fragment) {

        FragmentTransaction fragmentTransaction=getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_left,R.anim.slide_out_from_right);
        fragmentTransaction.replace(parentFrameLayout.getId(),fragment);
        fragmentTransaction.commit();
    }




}
