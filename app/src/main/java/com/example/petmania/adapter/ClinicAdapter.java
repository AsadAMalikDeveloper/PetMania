package com.example.petmania.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petmania.R;
import com.example.petmania.activities.BranchesActivity;
import com.example.petmania.model.Clinic;
import com.example.petmania.utils.Common;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class ClinicAdapter extends RecyclerView.Adapter<ClinicAdapter.ViewHolder> {

    private Context context;
    private List<Clinic>clinicList;
    private CompositeDisposable compositeDisposable =new CompositeDisposable();

    public ClinicAdapter(Context context, List<Clinic> clinicList) {
        this.context = context;
        this.clinicList = clinicList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.clinic_row_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.clinicName.setText(clinicList.get(position).getClinic_name());

        compositeDisposable.add(Common.getAPI().getAllBranches(clinicList.get(position).getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(branches -> {
                    if (branches.size()==1){
                        holder.branchCount.setText(new StringBuilder(""+branches.size()).append(" Branch"));
                    }else{
                        holder.branchCount.setText(new StringBuilder(""+branches.size()).append(" Branches"));
                    }
                }));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Common.SelectedClinicName = clinicList.get(position).getClinic_name();
                Intent intent = new Intent(context, BranchesActivity.class);
                intent.putExtra("clinic_name",clinicList.get(position).getClinic_name());
                intent.putExtra("clinic_id",clinicList.get(position).getId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return clinicList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView clinicName,branchCount;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            clinicName = itemView.findViewById(R.id.clinic_name_tv);
            branchCount = itemView.findViewById(R.id.branch_count);
        }
    }
}
