package com.example.petmania.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.petmania.R;
import com.example.petmania.adapter.ChatsConversationAdapter;
import com.example.petmania.adapter.ClinicAdapter;
import com.example.petmania.model.Adds;
import com.example.petmania.model.Chatlist;
import com.example.petmania.utils.Common;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class ClinicFragment extends Fragment {

    public ClinicFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        if(Common.bottomNavigationView!=null){
            Common.bottomNavigationView.setVisibility(View.GONE);
        }
    }


    private RecyclerView clinicRecyclerView;
    private ClinicAdapter clinicAdapter;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_clinic, container, false);

        clinicRecyclerView = view.findViewById(R.id.clinic_recyclerView);
        clinicRecyclerView.setHasFixedSize(true);
        clinicRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        compositeDisposable.add(Common.getAPI().getAllClinics()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(clinicList -> {
                    clinicAdapter = new ClinicAdapter(getContext(),clinicList);
                    clinicRecyclerView.setAdapter(clinicAdapter);
                }));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }
}
