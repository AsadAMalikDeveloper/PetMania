package com.example.petmania.fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.esotericsoftware.kryo.NotNull;
import com.example.petmania.R;
import com.example.petmania.activities.AddressActivity;
import com.example.petmania.activities.ImagesSelectionActivity;
import com.example.petmania.activities.VerifyPhoneActivity;
import com.example.petmania.adapter.AddressAdapter;
import com.example.petmania.adapter.AddsAdapter;
import com.example.petmania.adapter.CategoryAdapter;
import com.example.petmania.model.Addresses;
import com.example.petmania.model.Adds;
import com.example.petmania.model.Adds_images;
import com.example.petmania.model.Category;
import com.example.petmania.model.CheckUserResponse;
import com.example.petmania.prevalent.Prevalent;
import com.example.petmania.retrofit.IPetManiaAPI;
import com.example.petmania.utils.Common;
import com.example.petmania.utils.MySwipeHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;
import io.paperdb.Paper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.petmania.adapter.AddsAdapter.adPosition;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddsFragment extends Fragment {


    private GridView gridView;
    public static List<Category> categoryList;
    private IPetManiaAPI mServices = Common.getAPI();
    LinearLayout noAdd_layout, category_layout;
    private Button postBtn;
    private TextView okTxt;
    private AlertDialog alertDialog1;
    private RecyclerView recyclerView_adds;
    private  Button addnewAdBtn;
    private SwipeRefreshLayout swipeRefreshLayout;

    CompositeDisposable compositeDisposable = new CompositeDisposable();

    private void gotoPhoneVerificationActivity() {
        startActivity(new Intent(getContext(), VerifyPhoneActivity.class));
    }


    public AddsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        Category category1 = Paper.book().read(Prevalent.categoty,null);
        if (category1!=null) {
            AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Unfilled Data")
                    .setMessage("It's look Like you closed the app without completing the form\n you want to resume?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //set what would happen when positive button is clicked
                            gotoNext();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //set what should happen when negative button is clicked
                            Paper.book().delete(Prevalent.categoty);
                            Paper.book().delete(Prevalent.address);
                            Paper.book().delete(Prevalent.adds);
                        }
                    }).show();
        }

    }

    private void gotoNext() {

        Intent intent = new Intent(getActivity(), AddressActivity.class);
        startActivity(intent);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_adds, container, false);


        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);

        noAdd_layout = view.findViewById(R.id.noAdd_layout);
        category_layout = view.findViewById(R.id.category_layout);
        postBtn = view.findViewById(R.id.post_btn);
        gridView = view.findViewById(R.id.grid_view);
        recyclerView_adds = view.findViewById(R.id.recycler_adds);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        manager.setOrientation(RecyclerView.VERTICAL);
        recyclerView_adds.setLayoutManager(manager);
        addnewAdBtn = view.findViewById(R.id.addnewBtn);

        return view;
    }
    @Override
    public void onDestroy() {
        compositeDisposable.dispose();
        super.onDestroy();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Paper.init(getActivity());
        addnewAdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Paper.book().write(Prevalent.isEdit,"false");
                recyclerView_adds.setVisibility(View.GONE);
                addnewAdBtn.setVisibility(View.GONE);
                noAdd_layout.setVisibility(View.GONE);
                gridView.setVisibility(View.VISIBLE);
                category_layout.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setEnabled(false);
                showCat();
            }
        });
        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                showAdds();
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                showAdds();
            }
        });
    }

    private void showAdds() {
        alertDialog1 = new SpotsDialog.Builder()
                .setContext(getActivity())
                .setTheme(R.style.loading)
                .build();
        alertDialog1.show();
        if (Common.currentUser != null) {
            if (TextUtils.isEmpty(Common.currentUser.getPhone())) {
                gotoPhoneVerificationActivity();
            } else {
                mServices.checkExistAdds(String.valueOf(Common.currentUser.getUser_id())).enqueue(new Callback<CheckUserResponse>() {
                    @Override
                    public void onResponse(Call<CheckUserResponse> call, Response<CheckUserResponse> response) {
                        CheckUserResponse checkUserResponse = response.body();
                        if (checkUserResponse.isExists()){
                            swipeRefreshLayout.setRefreshing(false);
                            addnewAdBtn.setVisibility(View.VISIBLE);
                            recyclerView_adds.setVisibility(View.VISIBLE);
                            noAdd_layout.setVisibility(View.GONE);
                            compositeDisposable.add(mServices.getAdds(Common.currentUser.getUser_id())
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Consumer<List<Adds>>() {
                                        @Override
                                        public void accept(List<Adds> adds) throws Exception {
                                            alertDialog1.dismiss();
                                            AddsAdapter addsAdapter = new AddsAdapter(getContext(), adds);
                                            recyclerView_adds.setAdapter(addsAdapter);
                                            addsAdapter.notifyDataSetChanged();

                                            //GetSize
                                            DisplayMetrics displayMetrics = new DisplayMetrics();
                                            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                                            int width = displayMetrics.widthPixels;

                                            MySwipeHelper mySwipeHelper = new MySwipeHelper(getContext(),recyclerView_adds,width/6) {
                                                @Override
                                                public void instantiateMyButton(RecyclerView.ViewHolder viewHolder, List<MyButton> buf) {
                                                    buf.add(new MyButton(getContext(),"Delete",30,0, Color.RED,pos -> {
                                                        if (adPosition !=-1)
                                                            deleteAd(adPosition);
                                                            addsAdapter.notifyDataSetChanged();
                                                    }));
                                                    buf.add(new MyButton(getContext(),"Edit",30,0, Color.GRAY,pos -> {
                                                        if(adPosition!=-1){
                                                            Paper.book().write(Prevalent.isEdit,"true");
                                                            editAd(adPosition);
                                                        }else{
                                                            Toast.makeText(getContext(), "SomeThing Wrong", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }));

                                                }
                                            };
                                        }
                                    }));
                        }else{
                            swipeRefreshLayout.setRefreshing(false);
                            alertDialog1.dismiss();
                            addnewAdBtn.setVisibility(View.GONE);
                            recyclerView_adds.setVisibility(View.GONE);
                            noAdd_layout.setVisibility(View.VISIBLE);
                            postBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Paper.book().write(Prevalent.isEdit,"false");
                                    noAdd_layout.setVisibility(View.GONE);
                                    gridView.setVisibility(View.VISIBLE);
                                    category_layout.setVisibility(View.VISIBLE);
                                    showCat();
                                }
                            });
                        }
                    }

                    @Override
                    public void onFailure(Call<CheckUserResponse> call, Throwable t) {
                        alertDialog1.dismiss();
                        Toast.makeText(getContext(), ""+t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }
    }

    private void editAd(int id) {
        alertDialog1 = new SpotsDialog.Builder()
                .setContext(getActivity())
                .setTheme(R.style.loading)
                .build();
        alertDialog1.show();
       mServices.getAdsByAdId(id).enqueue(new Callback<Adds>() {
           @Override
           public void onResponse(Call<Adds> call, Response<Adds> response) {
               Adds adds1 =response.body();
               if (TextUtils.isEmpty(adds1.getError_msg()))
               {
                   alertDialog1.dismiss();
                   Paper.book().write(Prevalent.editAd,adds1);
                   recyclerView_adds.setVisibility(View.GONE);
                   addnewAdBtn.setVisibility(View.GONE);
                   noAdd_layout.setVisibility(View.GONE);
                   gridView.setVisibility(View.VISIBLE);
                   category_layout.setVisibility(View.VISIBLE);
                   swipeRefreshLayout.setEnabled(false);
                   showCat();
               }else {
                   alertDialog1.dismiss();
                   Toast.makeText(getContext(), "" + response.body().getError_msg(), Toast.LENGTH_SHORT).show();
               }
           }

           @Override
           public void onFailure(Call<Adds> call, Throwable t) {
               alertDialog1.dismiss();
               Toast.makeText(getContext(), "THROWABLE "+t.getMessage(), Toast.LENGTH_SHORT).show();
           }
       });
    }

    private void deleteAd(int pos) {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Deleting the Ad")
                .setMessage("Are you sure you want to delete this ad?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //set what would happen when positive button is clicked
                        mServices.deleteAd(pos).enqueue(new Callback<CheckUserResponse>() {
                            @Override
                            public void onResponse(Call<CheckUserResponse> call, Response<CheckUserResponse> response) {
                                CheckUserResponse response1  = response.body();
                                if (response1.isExists()){
                                    Toast.makeText(getContext(), "Successfully Done", Toast.LENGTH_SHORT).show();
                                    getActivity().recreate();
                                }else{
                                    Toast.makeText(getContext(), "Error "+response1.getError_msg(), Toast.LENGTH_SHORT).show();
                                    getActivity().recreate();
                                }
                            }

                            @Override
                            public void onFailure(Call<CheckUserResponse> call, Throwable t) {
                                getActivity().recreate();
                            }
                        });
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //set what should happen when negative button is clicked
                        dialogInterface.dismiss();
                    }
                }).show();
    }

    private void showCat() {
        compositeDisposable.add(mServices.getCategories()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(categoties -> {
                    CategoryAdapter categoryAdapter = new CategoryAdapter(getContext(), categoties,true);
                    gridView.setAdapter(categoryAdapter);
                    Log.d("AddsFragment", "accept: categories " + categoties.size());
                }));
    }

}
