package com.example.petmania.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petmania.R;
import com.example.petmania.activities.DoctorActivity;
import com.example.petmania.model.Branches;
import com.example.petmania.prevalent.Prevalent;
import com.example.petmania.utils.Common;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import io.paperdb.Paper;
import io.reactivex.disposables.CompositeDisposable;

public class BranchesAdapter extends RecyclerView.Adapter<BranchesAdapter.ViewHolder> implements Filterable {
    private Context context;
    private ArrayList<Branches> branchesList;
    private ArrayList<Branches> branchesListAll;
    private String clinicNameTxt;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public BranchesAdapter(Context context, ArrayList<Branches> branchesList,String clinicNameTxt) {
        this.context = context;
        this.branchesList = branchesList;
        this.clinicNameTxt = clinicNameTxt;
        this.branchesListAll = new ArrayList<>(branchesList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.branch_row_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        String close = changeTime(branchesList.get(position).getBr_close());
        String open = changeTime(branchesList.get(position).getBr_open());
            holder.clinicName.setText(clinicNameTxt);
        holder.branchAddress.setText(new StringBuilder(branchesList.get(position).getBr_address()));
        holder.branchCode.setText(new StringBuilder("Branch Code ").append(branchesList.get(position).getBr_code()));
        holder.branchCity.setText(branchesList.get(position).getBr_city());
        holder.closeTime.setText(new StringBuilder("Close : ").append(close));
        holder.openTime.setText(new StringBuilder("Open : ").append(open));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.SelectedBranchCode = branchesList.get(position).getBr_code();
                Paper.book().write(Prevalent.SelectedBranch,branchesList.get(position));
                Intent intent = new Intent(context, DoctorActivity.class);
                intent.putExtra("br_id",branchesList.get(position).getId());
                context.startActivity(intent);
            }
        });
    }

    private String changeTime(String time) {
        SimpleDateFormat code12Hours = new SimpleDateFormat("hh:mm"); // 12 hour format

        Date dateCode12 = null;

        String formatTwelve;
        try {
            dateCode12 = code12Hours.parse(time); // 12 hour
        } catch (ParseException e) {
            e.printStackTrace();
        }

        formatTwelve = code12Hours.format(dateCode12); // 12

        if (formatTwelve.equals(time)) {
             return formatTwelve + " AM";
        } else {
             return formatTwelve + " PM";
        }
    }


    @Override
    public int getItemCount() {
        return branchesList.size();
    }

    @Override
    public Filter getFilter() {
        return filter;
    }
    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            ArrayList<Branches> filterList  = new ArrayList<>();
            if (charSequence.toString().isEmpty()){
                filterList.addAll(branchesListAll);
            }else {
                for (Branches branches:branchesListAll){
                    if (branches.getBr_city().toLowerCase().contains(charSequence.toString().toLowerCase()) ||
                    branches.getBr_address().toLowerCase().contains(charSequence.toString().toLowerCase())){
                        filterList.add(branches);
                    }
                }
            }
            FilterResults filterResults =new FilterResults();
            filterResults.values = filterList;
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            branchesList.clear();
            branchesList.addAll((Collection<? extends Branches>) results.values);
            notifyDataSetChanged();
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView clinicName,branchAddress,branchCode,branchCity,openTime,closeTime;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            branchCity = itemView.findViewById(R.id.branch_city);
            clinicName = itemView.findViewById(R.id.clinic_name_tv);
            branchAddress = itemView.findViewById(R.id.branch_add);
            branchCode =  itemView.findViewById(R.id.branch_code);
            openTime = itemView.findViewById(R.id.open_time);
            closeTime = itemView.findViewById(R.id.close_time);
        }
    }
}
