package com.example.petmania.adapter;

import android.content.Context;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.petmania.R;
import com.example.petmania.model.SelectedPetHelper;

import java.util.List;

public class SelectedPetAdapter extends RecyclerView.Adapter<SelectedPetAdapter.ViewHolder> {
    private Context context;
    private List<SelectedPetHelper> selectedPets;

    public SelectedPetAdapter(Context context, List<SelectedPetHelper> selectedPets) {
        this.context = context;
        this.selectedPets = selectedPets;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.selected_pet_row_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SelectedPetHelper helper= selectedPets.get(position);
        holder.header.setText(helper.getHeader());
        holder.desc.setText(helper.getDesc());
        Linkify.addLinks(holder.desc,Linkify.ALL);
        holder.itemView.setBackgroundDrawable(selectedPets.get(position).getBg_clr());
    }

    @Override
    public int getItemCount() {
        return selectedPets.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView header,desc;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            header = itemView.findViewById(R.id.header_tv);
            desc = itemView.findViewById(R.id.desc_tv);
        }
    }
}
