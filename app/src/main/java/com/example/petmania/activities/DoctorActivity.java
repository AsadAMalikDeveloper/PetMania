package com.example.petmania.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.petmania.R;
import com.example.petmania.adapter.BranchesAdapter;
import com.example.petmania.adapter.DoctorAdapter;
import com.example.petmania.utils.Common;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class DoctorActivity extends AppCompatActivity {

    private int br_id;
    private ImageButton backBtn;
    private TextView branchNameTv;
    private Toolbar toolbar;
    private RecyclerView doctorsRecycler;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private DoctorAdapter adapter;
    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.dispose();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor);
        toolbar = (androidx.appcompat.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        br_id = getIntent().getIntExtra("br_id",-1);

        backBtn = findViewById(R.id.back_btn);
        branchNameTv =findViewById(R.id.branch_name_tv);
        doctorsRecycler = findViewById(R.id.recycler_doctors);
        doctorsRecycler.setHasFixedSize(true);
        doctorsRecycler.setLayoutManager(new LinearLayoutManager(this));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,DividerItemDecoration.VERTICAL);
        doctorsRecycler.addItemDecoration(dividerItemDecoration);

        branchNameTv.setText("Branch "+Common.SelectedBranchCode);

        compositeDisposable.add(Common.getAPI().getAllDoctors(br_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(doctors -> {
                    adapter= new DoctorAdapter(DoctorActivity.this,doctors);
                    doctorsRecycler.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }));

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
