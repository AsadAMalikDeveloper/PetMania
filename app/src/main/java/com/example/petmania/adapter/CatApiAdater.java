package com.example.petmania.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petmania.R;
import com.example.petmania.activities.NewFeedsSelectedPetActivity;
import com.example.petmania.model.CatApiBreed;
import com.example.petmania.model.DogApiBreed;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CatApiAdater extends RecyclerView.Adapter<CatApiAdater.ViewHolder> implements Filterable {

    Context context ;
    List<CatApiBreed> catApiBreedList;
    List<CatApiBreed> catListAll;
    List<CatApiBreed> filterList;

    public CatApiAdater(Context context, List<CatApiBreed> catApiBreedList) {
        this.context = context;
        this.catApiBreedList = catApiBreedList;
        this.catListAll = new ArrayList<>(catApiBreedList);
        this.filterList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pet_images_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.petName.setText(catApiBreedList.get(position).getName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, ""+catApiBreedList.get(position).getId(), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(context, NewFeedsSelectedPetActivity.class);
                intent.putExtra("pet_name",catApiBreedList.get(position).getName());
                intent.putExtra("pet_id",catApiBreedList.get(position).getId());
                intent.putExtra("is_pet","CAT");
                context.startActivity(intent);
                //Toast.makeText(context, ""+dogApiBreedList.get(position).getId()+" "+dogApiBreed.height.getImperial(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public Filter getFilter() {
        return filter;
    }
    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            filterList = new ArrayList<>();
            if (charSequence.toString().isEmpty()){
                filterList.addAll(catListAll);
            }else {
                for (CatApiBreed catApiBreed:catListAll){
                    if (catApiBreed.getName().toLowerCase().contains(charSequence.toString().toLowerCase())){
                        filterList.add(catApiBreed);
                    }
                }
            }
            FilterResults filterResults =new FilterResults();
            filterResults.values = filterList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            catApiBreedList.clear();
            catApiBreedList.addAll((Collection<? extends CatApiBreed>) results.values);
            notifyDataSetChanged();
        }
    };

    @Override
    public int getItemCount() {
        return catApiBreedList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView petName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            petName = itemView.findViewById(R.id.pet_name_tv);
        }
    }
}
