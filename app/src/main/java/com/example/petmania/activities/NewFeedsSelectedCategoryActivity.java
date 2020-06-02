package com.example.petmania.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.petmania.R;
import com.example.petmania.adapter.PetImageAdapter;
import com.example.petmania.model.DogApiBreed;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewFeedsSelectedCategoryActivity extends AppCompatActivity {

    private TextView catName;
    private ImageButton backBtn;
    private ImageView catImg;

    private List<DogApiBreed> list;
    private RecyclerView recyclerView;
    private PetImageAdapter adapter;
    private EditText searchEt;
    private TextView showAllTv;
    String name,image;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_feeds_selected_category);

        name = getIntent().getStringExtra("cat_name");
        image = getIntent().getStringExtra("cat_img");
        recyclerView = findViewById(R.id.petRv);
        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(2,LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(manager);


        backBtn = findViewById(R.id.back_btn);
        catImg = findViewById(R.id.cat_Iv);
        showAllTv = findViewById(R.id.filterPetsTv);
        catName = findViewById(R.id.cat_nameTv);
        searchEt = findViewById(R.id.searchDogEt);
        list = new ArrayList<>();

        catName.setText(name);
        Picasso.get().load(image).placeholder(R.drawable.load).into(catImg);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                search(s.toString());
            }
        });


    }

    private void search(String name) {
        list.clear();
        AndroidNetworking.initialize(getApplicationContext());


        AndroidNetworking.get("https://dog.ceo/api/breed/"+name+"/images")
                .addQueryParameter("limit","1")
        .setPriority(Priority.HIGH)
        .build()
        .getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONArray images =new JSONObject(response).getJSONArray("message");

                    for (int i = 0;i<images.length();i++){
                        String image = String.valueOf(images.get(i));
                        list.add(new DogApiBreed(image));
                    }
                    showAllTv.setText("Showing All ("+list.size()+")");
                    adapter = new PetImageAdapter(NewFeedsSelectedCategoryActivity.this,list);
                    recyclerView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    Toast.makeText(NewFeedsSelectedCategoryActivity.this, "Message "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onError(ANError anError) {

            }
        });
    }
}
