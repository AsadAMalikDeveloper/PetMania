package com.example.petmania.fragments;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.petmania.R;
import com.example.petmania.adapter.ShowAllAdsAdapter;
import com.example.petmania.database.datasource.FavouriteRespository;
import com.example.petmania.database.local.FavouriteDataSource;
import com.example.petmania.database.local.PetManiaRoomDatabase;
import com.example.petmania.eventbus.AdsItemClick;
import com.example.petmania.model.Adds;
import com.example.petmania.model.CheckUserResponse;
import com.example.petmania.retrofit.IPetManiaAPI;
import com.example.petmania.utils.Common;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {



    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
            if(Common.bottomNavigationView!=null){
                Common.bottomNavigationView.setVisibility(View.VISIBLE);
            }
        load();
    }

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private IPetManiaAPI mServices;
    private RecyclerView recyclerViewShowAds;
    private TextView nopostText,addsTv;
    private ImageView noPostImg;
    private SwipeRefreshLayout swipeRefreshLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_home, container, false);
        initViews(view);
        load();
//        Calendar calendar =  Calendar.getInstance();
//        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
//        String date = df.format(calendar.getTime());
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                load();
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                load();
            }
        });

        initDB();
        return view;
    }

    private void checkConnection() {
        ConnectivityManager manager = (ConnectivityManager)
                getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = manager.getActiveNetworkInfo();
        if (null!=activeNetwork){
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI){
                Toast.makeText(getContext(), "WIFI enabled", Toast.LENGTH_SHORT).show();
                load();
            }else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE){
                Toast.makeText(getContext(), "Data network Enabled", Toast.LENGTH_SHORT).show();
                load();
            }
        }else{
            Toast.makeText(getContext(), "No", Toast.LENGTH_SHORT).show();
        }
    }

    private void initDB() {
        Common.roomDatabase = PetManiaRoomDatabase.getDatabase(getContext());
        Common.favouriteRespository = FavouriteRespository.getInstance(FavouriteDataSource.getInstance(Common.roomDatabase.dao()));
    }

    private void load() {

        mServices.checkAllAds(Common.currentUser.getUser_id()).enqueue(new Callback<CheckUserResponse>() {
            @Override
            public void onResponse(Call<CheckUserResponse> call, Response<CheckUserResponse> response) {
                CheckUserResponse response1 = response.body();
                if (response1.isExists()){
                    compositeDisposable.add(mServices.getAllAds(Common.currentUser.getUser_id())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<List<Adds>>() {
                                @Override
                                public void accept(List<Adds> adds) throws Exception {

                                    swipeRefreshLayout.setRefreshing(false);
                                    Log.d("HomeFragment ", "@@@!!!!else: "+adds);
                                    nopostText.setVisibility(View.GONE);
                                    noPostImg.setVisibility(View.GONE);
                                    addsTv.setVisibility(View.VISIBLE);
                                    recyclerViewShowAds.setVisibility(View.VISIBLE);
                                    ShowAllAdsAdapter showAllAdsAdapter = new ShowAllAdsAdapter(getContext(),adds);
                                    recyclerViewShowAds.setAdapter(showAllAdsAdapter);
                                    showAllAdsAdapter.notifyDataSetChanged();
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(Throwable throwable) throws Exception {

                                    swipeRefreshLayout.setRefreshing(false);
                                    Toast.makeText(getContext(), "[THROWABLE]  "+throwable.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            }));

                }else{
                    swipeRefreshLayout.setRefreshing(false);
                    nopostText.setVisibility(View.VISIBLE);
                    noPostImg.setVisibility(View.VISIBLE);
                    addsTv.setVisibility(View.GONE);
                    recyclerViewShowAds.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<CheckUserResponse> call, Throwable t) {

                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getContext(), "[FAILURE THROWABLE]  "+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViews(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        noPostImg = view.findViewById(R.id.no_post_img);
        nopostText = view.findViewById(R.id.no_post_txt);
        addsTv = view.findViewById(R.id.txt_ads);
        mServices = Common.getAPI();
        recyclerViewShowAds = view.findViewById(R.id.recycler_showAdds);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerViewShowAds.setLayoutManager(manager);
        recyclerViewShowAds.addItemDecoration(new DividerItemDecoration(getContext(),manager.getOrientation()));
    }


    @Override
    public void onResume() {
        super.onResume();
        load();
    }

    @Override
    public void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }


}
