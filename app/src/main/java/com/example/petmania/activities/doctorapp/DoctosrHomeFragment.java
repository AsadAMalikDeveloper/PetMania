package com.example.petmania.activities.doctorapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.petmania.R;
import com.example.petmania.utils.Common;

public class DoctosrHomeFragment extends Fragment {


    public DoctosrHomeFragment() {
        // Required empty public constructor
    }

    private TextView drName;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_doctosr_home, container, false);
        drName = view.findViewById(R.id.dr_name_home);
        drName.setText(Common.currentDoctor.getDr_name());
        return view;
    }
}