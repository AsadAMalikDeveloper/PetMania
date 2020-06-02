package com.example.petmania.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;

import com.example.petmania.R;
import com.example.petmania.adapter.BranchesAdapter;
import com.example.petmania.utils.Common;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class BranchesActivity extends AppCompatActivity {

    private ImageButton backBtn;
    private TextView clinicNameTv;
    private int clinicId;
    private  String clinicName="";
    private RecyclerView branchRecyclerView;
    private BranchesAdapter adapter;
    private CompositeDisposable compositeDisposable= new CompositeDisposable();
    private Toolbar toolbar;
    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branches);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        clinicId = getIntent().getIntExtra("clinic_id",0);
        clinicName = getIntent().getStringExtra("clinic_name");
        backBtn = findViewById(R.id.back_btn);
        clinicNameTv =findViewById(R.id.clinic_name_tv);
        branchRecyclerView = findViewById(R.id.recycler_branches);
        branchRecyclerView.setHasFixedSize(true);
        branchRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        branchRecyclerView.addItemDecoration(dividerItemDecoration);
        compositeDisposable.add(Common.getAPI().getAllBranches(clinicId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(branches -> {
                    adapter= new BranchesAdapter(this,branches,clinicName);
                    branchRecyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }));

        clinicNameTv.setText(clinicName);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu,menu);
        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setIconifiedByDefault(true);
        searchView.setFocusable(true);
        searchView.setIconified(false);
        searchView.requestFocusFromTouch();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}
