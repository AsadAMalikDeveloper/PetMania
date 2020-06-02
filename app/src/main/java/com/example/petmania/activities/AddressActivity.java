package com.example.petmania.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.petmania.R;
import com.example.petmania.adapter.AddressAdapter;
import com.example.petmania.fragments.AddsFragment;
import com.example.petmania.model.Addresses;
import com.example.petmania.model.Adds;
import com.example.petmania.model.Category;
import com.example.petmania.model.CheckUserResponse;
import com.example.petmania.prevalent.Prevalent;
import com.example.petmania.retrofit.IPetManiaAPI;
import com.example.petmania.utils.Common;
import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressActivity extends AppCompatActivity {
    RecyclerView addressRV;
    AddressAdapter addressAdapter;
    List<Addresses> addresses;
    IPetManiaAPI mServices;
    private RelativeLayout relativeLayoutContainer;
    Button nextBtn;
    TextView addressNotFoundtv;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    @Override
    protected void onStart() {
        super.onStart();

        Addresses addresses1 = Paper.book().read(Prevalent.address, null);
        if (addresses1 != null) {
            gotoDetailsActivity();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        Paper.init(this);
        mServices = Common.getAPI();
        nextBtn = findViewById(R.id.address_nextBtn);
        addressNotFoundtv = findViewById(R.id.address_notFoundTV);
        relativeLayoutContainer = findViewById(R.id.addnewAddress_container);
        relativeLayoutContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoAddnewAddress();
            }
        });

        addressRV = findViewById(R.id.address_recycler);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(RecyclerView.VERTICAL);
        addressRV.setLayoutManager(manager);


        mServices.checkExistAddress(String.valueOf(Common.currentUser.getUser_id())).enqueue(new Callback<CheckUserResponse>() {
            @Override
            public void onResponse(Call<CheckUserResponse> call, Response<CheckUserResponse> response) {
                CheckUserResponse checkUserResponse = response.body();
                if (checkUserResponse.isExists()) {

                    addressRV.setVisibility(View.VISIBLE);
                    nextBtn.setVisibility(View.VISIBLE);
                    compositeDisposable.add(mServices.getAddresses(Common.currentUser.getUser_id())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<List<Addresses>>() {
                                @Override
                                public void accept(List<Addresses> addresses) throws Exception {
                                    addressAdapter = new AddressAdapter(addresses, AddressActivity.this);
                                    addressRV.setAdapter(addressAdapter);
                                    addressAdapter.notifyDataSetChanged();
                                }
                            }));
                } else {

                    addressNotFoundtv.setVisibility(View.VISIBLE);
                    addressRV.setVisibility(View.GONE);
                    nextBtn.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<CheckUserResponse> call, Throwable t) {
                Toast.makeText(AddressActivity.this, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Paper.book().delete(Prevalent.categoty);
        Paper.book().delete(Prevalent.address);
    }

    private void gotoAddnewAddress() {
        Intent intent = new Intent(AddressActivity.this, AddnewAddressActivity.class);
        startActivity(intent);
    }

    public void selectedAddress(View view) {
        if (Common.currentAddres == null) {
            Toast.makeText(this, "select one address", Toast.LENGTH_SHORT).show();
        } else {

            Toast.makeText(this, "" + Common.currentAddres.getAddress_id(), Toast.LENGTH_SHORT).show();
            gotoDetailsActivity();
        }
    }

    private void gotoDetailsActivity() {
        Intent intent = new Intent(AddressActivity.this, DescriptionActivity.class);
        startActivity(intent);
    }
}
