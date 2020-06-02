package com.example.petmania.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.petmania.R;
import com.example.petmania.activities.AddressActivity;
import com.example.petmania.activities.DescriptionActivity;
import com.example.petmania.activities.NewFeedsSelectedCategoryActivity;
import com.example.petmania.model.Category;
import com.example.petmania.prevalent.Prevalent;
import com.example.petmania.utils.Common;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class CategoryAdapter extends BaseAdapter {

    List<Category> categoryList;
    Context context;
    boolean isAd = true;

    public CategoryAdapter(Context context,List<Category> categoryList,boolean isAd) {
        this.context = context;
        this.categoryList = categoryList;
        this.isAd = isAd;
    }

    @Override
    public int getCount() {
       return categoryList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if(convertView==null){
            view= LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item_layout,null);

            view.setElevation(2);
            view.setBackgroundColor(Color.parseColor("#ffffff"));
            Paper.init(context);
            if (isAd){
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, ""+categoryList.get(position).getCategory_id(), Toast.LENGTH_SHORT).show();
                        Common.currentCategory = categoryList.get(position);
                        Paper.book().write(Prevalent.categoty, categoryList.get(position));
                        Intent intent = new Intent(context, AddressActivity.class);
                        context.startActivity(intent);
                    }
                });
            }else {
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(context, NewFeedsSelectedCategoryActivity.class);
                        intent.putExtra("cat_name",categoryList.get(position).getCategory_name());
                        intent.putExtra("cat_img",categoryList.get(position).getCategory_image());
                        context.startActivity(intent);
                    }
                });
            }

            ImageView productImage=view.findViewById(R.id.category_Image);
            TextView productTitle=view.findViewById(R.id.category_title);

           Glide.with(parent.getContext()).load(categoryList.get(position).getCategory_image()).apply(new RequestOptions().placeholder(R.drawable.load)).into(productImage);

            productTitle.setText(categoryList.get(position).getCategory_name());
        }else{

            view=convertView;
        }
        return view;
    }
}
