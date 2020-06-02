package com.example.petmania.fragments;


import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.example.petmania.R;
import com.example.petmania.adapter.FavouriteListAdapter;
import com.example.petmania.callback.RecyclerItemTouchHelperListener;
import com.example.petmania.database.local.PetManiaRoomDatabase;
import com.example.petmania.database.modeldb.Favourite;
import com.example.petmania.retrofit.IPetManiaAPI;
import com.example.petmania.utils.Common;
import com.example.petmania.utils.RecyclerItemTouchHelper;
import com.google.android.material.snackbar.Snackbar;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavouriteFragment extends Fragment implements RecyclerItemTouchHelperListener {


    public FavouriteFragment() {
        // Required empty public constructor
    }

    RelativeLayout rootLayout;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private IPetManiaAPI mServices;
    private RecyclerView recyclerViewFavAds;
    private ImageView noPostImg;
    private SwipeRefreshLayout swipeRefreshLayout;
    FavouriteListAdapter adapter;
    List<Favourite> localFavouriteList = new ArrayList<>();
    LottieAnimationView favAnimation;
    TextView animTxt;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favourite, container, false);
        initViews(view);
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

        return view;


    }

    private void load() {


        compositeDisposable.add(Common.favouriteRespository.getFavItems()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(favouriteList -> {
                    if (favouriteList.isEmpty()){

                        swipeRefreshLayout.setVisibility(View.GONE);
                        animTxt.setVisibility(View.VISIBLE);
                        favAnimation.setVisibility(View.VISIBLE);
                    }else{
                        swipeRefreshLayout.setVisibility(View.VISIBLE);
                        animTxt.setVisibility(View.GONE);
                        favAnimation.setVisibility(View.GONE);
                        swipeRefreshLayout.setRefreshing(false);
                        localFavouriteList = favouriteList;
                        adapter = new FavouriteListAdapter(getContext(),favouriteList);
                        recyclerViewFavAds.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {

                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(getContext(), "[THROWABLE]  "+throwable.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                }));



        /*swipeRefreshLayout.setRefreshing(false);
        nopostText.setVisibility(View.VISIBLE);
        noPostImg.setVisibility(View.VISIBLE);
        addsTv.setVisibility(View.GONE);
        recyclerViewShowAds.setVisibility(View.GONE);*/
    }

    @Override
    public void onResume() {
        super.onResume();
        load();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }

    private void initViews(View view) {
        favAnimation = view.findViewById(R.id.fav_animation);
        animTxt = view.findViewById(R.id.anim_txt);
        rootLayout = view.findViewById(R.id.rootLayout);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout1);
        mServices = Common.getAPI();
        recyclerViewFavAds = view.findViewById(R.id.recycler_fav);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerViewFavAds.setLayoutManager(manager);
        recyclerViewFavAds.addItemDecoration(new DividerItemDecoration(getContext(),manager.getOrientation()));


        ItemTouchHelper.SimpleCallback simpleCallback = new RecyclerItemTouchHelper(0,ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerViewFavAds);
    }

    @Override
    public void onSwipe(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof FavouriteListAdapter.ViewHolder){
            String name = localFavouriteList.get(viewHolder.getAdapterPosition()).title;

            Favourite deleteItem = localFavouriteList.get(viewHolder.getAdapterPosition());
            int deleteIndex = viewHolder.getAdapterPosition();

            //delete item from adapter
            adapter.removeItem(deleteIndex);

            //delete from roomDB
            Common.favouriteRespository.delete(deleteItem);

            Snackbar snackbar = Snackbar.make(rootLayout,new StringBuilder(name).append(" removed from favourites list").toString(),
                    Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.restoreItem(deleteItem,deleteIndex);
                    Common.favouriteRespository.insertFav(deleteItem);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
            if(Common.bottomNavigationView!=null){
                Common.bottomNavigationView.setVisibility(View.GONE);
            }
        }
}
