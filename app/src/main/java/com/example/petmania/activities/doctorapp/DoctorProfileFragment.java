package com.example.petmania.activities.doctorapp;

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
import com.example.petmania.utils.Common;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.scottyab.aescrypt.AESCrypt;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import io.paperdb.Paper;

public class DoctorProfileFragment extends Fragment {


    public DoctorProfileFragment() {
        // Required empty public constructor
    }

    private TextView textView,userName;
    RelativeLayout showRL;
    ScrollView editSV;
    ImageView close,phonePin,emailPin;
    MaterialEditText userNameEdt,emailEdt,phoneEdt,password_et,dr_spec;
    String password=null;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_doctor_profile2, container, false);
        init(view);
        setValues();
        return view;
    }
    private void setValues() {
        try {
            password = AESCrypt.decrypt("hello", Common.currentDoctor.getDr_pass());
        }catch (GeneralSecurityException e){
            //handle error - could be due to incorrect password or tampered encryptedMsg
        }
        if (password!=null)
            password_et.setText(password);
        emailEdt.setText(Common.currentDoctor.getDr_email().toLowerCase());
        if (!TextUtils.isEmpty(Common.currentDoctor.getDr_phone())){
            phoneEdt.setText(Common.currentDoctor.getDr_phone().substring(3));
        }else {
            phoneEdt.setHint("Update phone");
        }

        userNameEdt.setText(Common.currentDoctor.getDr_name());
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
        userName.setText(Common.currentDoctor.getDr_name());
        dr_spec.setText(Common.currentDoctor.getDr_speciality());
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
        userName = view.findViewById(R.id.doctor_name_account);
        textView = view.findViewById(R.id.textViewLink);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        showRL = view.findViewById(R.id.nameLayout_account);
        close = view.findViewById(R.id.close);
        editSV = view.findViewById(R.id.edit_scollViewLayout);
        userNameEdt = view.findViewById(R.id.doctor_account_et);
        emailEdt = view.findViewById(R.id.email_account_et_dr);
        dr_spec = view.findViewById(R.id.dr_speciality);
        phoneEdt = view.findViewById(R.id.edit_text_phone_dr);
        password_et = view.findViewById(R.id.password_et_dr);
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