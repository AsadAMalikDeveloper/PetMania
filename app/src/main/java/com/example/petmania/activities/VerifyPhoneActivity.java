package com.example.petmania.activities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.example.petmania.R;
import com.example.petmania.model.User;
import com.example.petmania.prevalent.Prevalent;
import com.example.petmania.retrofit.IPetManiaAPI;
import com.example.petmania.utils.Common;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.hbb20.CountryCodePicker;

import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerifyPhoneActivity extends AppCompatActivity {

    private LinearLayout layoutPhone, layoutVerification;
    private Button sendCode;
    private EditText phoneNum, codeTxt;
    private CountryCodePicker countryCodePicker;
    private String verificationID = null;
    private Double latitude = 0.0;
    private Double longitude = 0.0;
    LocationManager locationManager;
    private int permissionCode=1;
    IPetManiaAPI mServices;
    private String verifiedPhoneNumber=null,usernumber=null,phoneNumber=null;
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);
        mServices = Common.getAPI();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        layoutPhone = findViewById(R.id.layoutPhone);
        layoutVerification = findViewById(R.id.layoutVerification);
        sendCode = findViewById(R.id.button_send_verification);
        phoneNum = findViewById(R.id.edit_text_phone);
        countryCodePicker = findViewById(R.id.ccp);
        codeTxt = findViewById(R.id.edit_text_code);

        layoutPhone.setVisibility(View.VISIBLE);
        layoutVerification.setVisibility(View.GONE);


    }




    public void SendCode(View view) {
        usernumber = phoneNum.getText().toString().trim();

        if (usernumber.isEmpty() || usernumber.length()!=10){
            phoneNum.setError("Enter Valid Number");
            phoneNum.requestFocus();
            return;
        }
        phoneNumber = "+" +countryCodePicker.getSelectedCountryCode()+ usernumber;

        PhoneAuthProvider.getInstance()
                .verifyPhoneNumber(
                        phoneNumber,
                        60,
                        TimeUnit.SECONDS,
                        VerifyPhoneActivity.this,
                        mCallBack);
        layoutPhone.setVisibility(View.GONE);
        layoutVerification.setVisibility(View.VISIBLE);
    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallBack = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            if (phoneNumber!=null){
                verifiedPhoneNumber = phoneNumber;
                updateInfo(verifiedPhoneNumber);
                finish();
            }else{
                Toast.makeText(VerifyPhoneActivity.this, "SomeThing went Wrong", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(verificationId, forceResendingToken);
            verificationID = verificationId;
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(VerifyPhoneActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    public void verifyCode(View view) {
        String code = codeTxt.getText().toString();
        if (code.isEmpty()){
            codeTxt.setError("Enter Valid Code");
            codeTxt.requestFocus();
            return;
        }
        if (phoneNumber!=null){
            verifiedPhoneNumber = phoneNumber;
            updateInfo(verifiedPhoneNumber);
            finish();
        }else{
            Toast.makeText(this, "SomeThing went Wrong", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateInfo(String verifiedPhoneNumber){
       // String address = String.format(new StringBuilder(city).append(",").append(country).toString());
        mServices. updateInfo(Common.currentUser.getEmail(),verifiedPhoneNumber).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User user =response.body();
                if (TextUtils.isEmpty(user.getError_msg())){
                    Toast.makeText(VerifyPhoneActivity.this, "Phone Updated", Toast.LENGTH_SHORT).show();
                    Common.currentUser = response.body();
                }else{
                    Toast.makeText(VerifyPhoneActivity.this, user.getError_msg(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(VerifyPhoneActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

}
