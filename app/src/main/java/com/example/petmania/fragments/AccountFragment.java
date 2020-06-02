package com.example.petmania.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.petmania.R;
import com.example.petmania.prevalent.Prevalent;
import com.example.petmania.utils.Common;
import com.luseen.spacenavigation.SpaceNavigationView;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.scottyab.aescrypt.AESCrypt;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import io.paperdb.Paper;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccountFragment extends Fragment{


    @Override
    public void onStart() {
        super.onStart();
        if(Common.bottomNavigationView!=null){
            Common.bottomNavigationView.setVisibility(View.GONE);
        }
    }

    public AccountFragment() {
        // Required empty public constructor
    }


    private TextView textView,userName;
    RelativeLayout showRL;
    ScrollView editSV;
    ImageView close,phonePin,emailPin;
    MaterialEditText userNameEdt,emailEdt,phoneEdt,password_et;



    SpaceNavigationView spaceNavigationView;
    String password=null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_account, container, false);

        init(view);
        setValues();
        return view;
    }

    private void setValues() {
        try {
            password = AESCrypt.decrypt("hello", Common.currentUser.getPassword());
        }catch (GeneralSecurityException e){
            //handle error - could be due to incorrect password or tampered encryptedMsg
        }
        if (password!=null)
            password_et.setText(password);
        emailEdt.setText(Common.currentUser.getEmail().toLowerCase());
        if (!TextUtils.isEmpty(Common.currentUser.getPhone())){
            phoneEdt.setText(Common.currentUser.getPhone().substring(3));
        }else {
            phoneEdt.setHint("Update phone");
        }

        userNameEdt.setText(Common.currentUser.getName());
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRL.setVisibility(View.GONE);
                editSV.setVisibility(View.VISIBLE);
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                showRL.setVisibility(View.VISIBLE);
                editSV.setVisibility(View.GONE);
            }
        });
        userName.setText(Common.currentUser.getName());

        emailPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Email ok", Toast.LENGTH_SHORT).show();
            }
        });

        phonePin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext(), "Phone ok", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void init(View view) {
        Paper.init(getContext());
        userName = view.findViewById(R.id.userName_account);
        textView = view.findViewById(R.id.textViewLink);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        showRL = view.findViewById(R.id.nameLayout_account);
        close = view.findViewById(R.id.close);
        editSV = view.findViewById(R.id.edit_scollViewLayout);
        userNameEdt = view.findViewById(R.id.userName_account_et);
        emailEdt = view.findViewById(R.id.email_account_et);
        phoneEdt = view.findViewById(R.id.edit_text_phone);
        password_et = view.findViewById(R.id.password_et);
        phonePin = view.findViewById(R.id.phone_pastePin);
        emailPin = view.findViewById(R.id.email_pastePin);
    }


    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest
                    .getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

}
