package com.example.petmania.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petmania.R;
import com.example.petmania.model.DogApiBreed;
import com.squareup.picasso.Picasso;

import java.util.List;

public class PetImageAdapter extends RecyclerView.Adapter<PetImageAdapter.ViewHolder> {

    Context context ;
    List<DogApiBreed> dogApiBreedList;

    public PetImageAdapter(Context context, List<DogApiBreed> dogApiBreedList) {
        this.context = context;
        this.dogApiBreedList = dogApiBreedList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pet_images_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Picasso.get().load(dogApiBreedList.get(position).getMessage()).placeholder(R.drawable.load).into(holder.petImg);
    }

    @Override
    public int getItemCount() {
        return dogApiBreedList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView petImg;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            petImg = itemView.findViewById(R.id.pet_img);
        }
    }
}
