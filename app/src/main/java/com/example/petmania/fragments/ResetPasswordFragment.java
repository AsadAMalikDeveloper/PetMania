package com.example.petmania.fragments;


import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;

import com.example.petmania.R;
import com.example.petmania.model.CheckUserResponse;
import com.example.petmania.retrofit.IPetManiaAPI;
import com.example.petmania.utils.Common;

import javax.annotation.Nullable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A simple {@link Fragment} subclass.
 */
public class ResetPasswordFragment extends androidx.fragment.app.Fragment {


    public ResetPasswordFragment() {
        // Required empty public constructor
    }


    private EditText email;
    private Button resetBtn;
    private TextView goBack;
    private FrameLayout parentFrameLayout;

    private ViewGroup emailIconContainer;
    private ImageView emailIcon;
    private TextView emailIconText;
    private ProgressBar progressBar;

    IPetManiaAPI mServices;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_reset_password, container, false);


        mServices = Common.getAPI();
        email=view.findViewById(R.id.fp_email);
        resetBtn=view.findViewById(R.id.fp_resetBtn);
        goBack=view.findViewById(R.id.fp_goBack);
        parentFrameLayout=getActivity().findViewById(R.id.register_frameLayout);

        emailIconContainer=view.findViewById(R.id.fp_linearLayout_container);
        emailIcon=view.findViewById(R.id.fp_email_icon);
        emailIconText=view.findViewById(R.id.fp_email_icon_text);
        progressBar=view.findViewById(R.id.fp_progressBar);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

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
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setFregment(new SignInFragment());
            }
        });

        resetBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TransitionManager.beginDelayedTransition(emailIconContainer);
                emailIconText.setVisibility(View.GONE);
                TransitionManager.beginDelayedTransition(emailIconContainer);
                emailIcon.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                resetBtn.setEnabled(false);
                resetBtn.setTextColor(Color.argb(50,255,255,255));


                mServices.forgetPassword(email.getText().toString())
                        .enqueue(new Callback<CheckUserResponse>() {
                            @Override
                            public void onResponse(Call<CheckUserResponse> call, Response<CheckUserResponse> response) {
                                CheckUserResponse userResponse = response.body();
                                if (userResponse.isExists()){

                                    ScaleAnimation scaleAnimation = new ScaleAnimation(1,0,1,0,emailIcon.getWidth()/2,emailIcon.getHeight()/2);
                                    scaleAnimation.setDuration(100);
                                    scaleAnimation.setInterpolator(new AccelerateInterpolator());
                                    scaleAnimation.setRepeatMode(Animation.REVERSE);
                                    scaleAnimation.setRepeatCount(1);

                                    scaleAnimation.setAnimationListener(new Animation.AnimationListener(){
                                        @Override
                                        public void onAnimationStart(Animation animation) {

                                        }

                                        @Override
                                        public void onAnimationEnd(Animation animation) {
                                            progressBar.setVisibility(View.GONE);
                                            emailIconText.setText("Recovery email sent successfully ! check your inbox");
                                            emailIconText.setTextColor(getResources().getColor(R.color.successGreen));

                                            TransitionManager.beginDelayedTransition(emailIconContainer);
                                            emailIconText.setVisibility(View.VISIBLE);
                                        }

                                        @Override
                                        public void onAnimationRepeat(Animation animation) {

                                            emailIcon.setImageResource(R.drawable.green_email);
                                        }
                                    });

                                    emailIcon.startAnimation(scaleAnimation);
                                }else{
                                    progressBar.setVisibility(View.GONE);
                                    resetBtn.setEnabled(true);
                                    resetBtn.setTextColor(getResources().getColor(R.color.colorAccent));
                                    emailIconText.setText("Oh ! its look like "+email.getText().toString()+" Is Not In Our Favourite");
                                    emailIconText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                                    TransitionManager.beginDelayedTransition(emailIconContainer);
                                    emailIconText.setVisibility(View.VISIBLE);
                                }
                            }

                            @Override
                            public void onFailure(Call<CheckUserResponse> call, Throwable t) {
                                progressBar.setVisibility(View.GONE);
                                String errormsg=t.getMessage();
                                resetBtn.setEnabled(true);
                                resetBtn.setTextColor(getResources().getColor(R.color.colorAccent));
                                emailIconText.setText(errormsg);
                                emailIconText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                                TransitionManager.beginDelayedTransition(emailIconContainer);
                                emailIconText.setVisibility(View.VISIBLE);
                            }
                        });

            }
        });
    }

    private void checkInputs() {
        if(TextUtils.isEmpty(email.getText())){
            resetBtn.setEnabled(false);
            resetBtn.setTextColor(Color.argb(50,255,255,255));
        }else{

            resetBtn.setEnabled(true);
            resetBtn.setTextColor(getResources().getColor(R.color.colorAccent));
        }
    }

    private void setFregment(androidx.fragment.app.Fragment fragment) {

        FragmentTransaction fragmentTransaction=getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.slide_from_left,R.anim.slide_out_from_right);
        fragmentTransaction.replace(parentFrameLayout.getId(),fragment);
        fragmentTransaction.commit();
    }
}
