package com.example.petmania.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.petmania.R;
import com.example.petmania.adapter.CatApiAdater;
import com.example.petmania.adapter.DogApiAdapter;
import com.example.petmania.utils.Common;
import com.squareup.picasso.Picasso;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;


public class NewFeedsSelectedCategoryActivity extends AppCompatActivity {

    private TextView catName;
    private ImageButton backBtn;
    private ImageView catImg;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    private RecyclerView recyclerView;
    private DogApiAdapter adapter;
    private CatApiAdater catAdapter;
    private EditText searchEt;
    private TextView showAllTv;
    String name,image;

    @Override
    protected void onStop() {
        super.onStop();
        compositeDisposable.dispose();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_feeds_selected_category);

        name = getIntent().getStringExtra("cat_name");
        image = getIntent().getStringExtra("cat_img");
        recyclerView = findViewById(R.id.petRv);
       // StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2,LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));


        backBtn = findViewById(R.id.back_btn);
        catImg = findViewById(R.id.cat_Iv);
        showAllTv = findViewById(R.id.filterPetsTv);
        catName = findViewById(R.id.cat_nameTv);
        searchEt = findViewById(R.id.searchDogEt);

        catName.setText(name);
        Toast.makeText(this, ""+name, Toast.LENGTH_SHORT).show();
        Picasso.get().load(image).placeholder(R.drawable.load).into(catImg);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        if (name.equals("Dog")){
            searchDog();
        }else if (name.equals("Cat")){
            Toast.makeText(this, ""+name, Toast.LENGTH_SHORT).show();
            searchCat();
        }

        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (name.equals("Dog")){
                    adapter.getFilter().filter(s.toString().trim());
                }else if (name.equals("Cat")){
                    catAdapter.getFilter().filter(s.toString().trim());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void searchCat() {
        compositeDisposable.add(Common.getAPI().getAllCats()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(catApiBreedArrayList -> {
                    showAllTv.setText("Showing All ("+catApiBreedArrayList.size()+")");
                    catAdapter = new CatApiAdater(NewFeedsSelectedCategoryActivity.this,catApiBreedArrayList);
                    recyclerView.setAdapter(catAdapter);
                    catAdapter.notifyDataSetChanged();
                }));
    }

    private void searchDog() {

        compositeDisposable.add(Common.getAPI().getAllDogs()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(dogApiBreedArrayList -> {
                    showAllTv.setText("Showing All ("+dogApiBreedArrayList.size()+")");
                    adapter = new DogApiAdapter(NewFeedsSelectedCategoryActivity.this,dogApiBreedArrayList);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }));
    }
}
