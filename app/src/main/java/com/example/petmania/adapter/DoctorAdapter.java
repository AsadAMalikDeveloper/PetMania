package com.example.petmania.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
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
import com.example.petmania.activities.DoctorDetailActivity;
import com.example.petmania.model.Branches;
import com.example.petmania.model.Doctors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.ViewHolder> implements Filterable {
    Context context;
    ArrayList<Doctors> doctorsArrayList;
    ArrayList<Doctors> doctorsListAll;
    ArrayList<Doctors> filterList;

    public DoctorAdapter(Context context, ArrayList<Doctors> doctorsArrayList) {
        this.context = context;
        this.doctorsArrayList = doctorsArrayList;
        this.doctorsListAll = new ArrayList<>(doctorsArrayList);
        this.filterList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.doctor_row_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.drName.setText(doctorsArrayList.get(position).getDr_name());
        holder.drSpec.setText(new StringBuilder(doctorsArrayList.get(position).getDr_speciality()).append(" Specialist"));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if (filterList.size()>=1){
                  Intent intent = new Intent(context, DoctorDetailActivity.class);
                  intent.putExtra("SEARCHLIST", (Serializable) filterList);
                  intent.putExtra("isSearch",true);
                  intent.putExtra("searchPos",holder.getAdapterPosition());
                  context.startActivity(intent);
                }else {
                    Intent intent = new Intent(context, DoctorDetailActivity.class);
                    intent.putExtra("LIST", (Serializable) doctorsArrayList);
                    intent.putExtra("isSearch",false);
                    intent.putExtra("pos",holder.getAdapterPosition());
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return doctorsArrayList.size();
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
                filterList.addAll(doctorsListAll);
            }else {
                for (Doctors doctors:doctorsListAll){
                    if (doctors.getDr_name().toLowerCase().contains(charSequence.toString().toLowerCase()) ||
                            doctors.getDr_speciality().toLowerCase().contains(charSequence.toString().toLowerCase())){
                        filterList.add(doctors);
                    }
                }
            }
            FilterResults filterResults =new FilterResults();
            filterResults.values = filterList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            doctorsArrayList.clear();
            doctorsArrayList.addAll((Collection<? extends Doctors>) results.values);
            notifyDataSetChanged();
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView drName,drSpec;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            drName = itemView.findViewById(R.id.doctor_name_tv);
            drSpec = itemView.findViewById(R.id.doctor_spec);
        }
    }
}
