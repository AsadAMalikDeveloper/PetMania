package com.example.petmania.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.petmania.R;
import com.example.petmania.model.Adds;
import com.example.petmania.utils.Common;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class ShowUsersAdsAdapter extends RecyclerView.Adapter<ShowUsersAdsAdapter.ViewHolder> {
    Context context;
    List<Adds> addsList;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    private static final String TAG ="ShowUsersAdsAdapter";

    public ShowUsersAdsAdapter(Context context, List<Adds> addsList) {
        this.context = context;
        this.addsList = addsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_showadds_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        compositeDisposable.add(Common.getAPI().loadAddressWithAddId(addsList.get(position).getAddress_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(addresses -> {
                    holder.addressAdd.setText(new StringBuilder(addresses.get(position).getCountry()).append(", ").append(addresses.get(position).getCity()));
                }));
        compositeDisposable.add(Common.getAPI().getImages(addsList.get(position).getAdds_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(adds_images -> {
                    Glide.with(context).load("https://petsmaniapk.000webhostapp.com/functions/images/uploads/".concat(adds_images.get(position).getImage_url())).apply(new RequestOptions().placeholder(R.drawable.load)).into(holder.imgAdd);

                }));
        holder.nameAdd.setText(new StringBuilder(addsList.get(position).getAdd_title()));

    }

    @Override
    public int getItemCount() {
        return addsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAdd;
        TextView nameAdd;
        TextView addressAdd;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAdd = itemView.findViewById(R.id.img_add);
            nameAdd = itemView.findViewById(R.id.txt_add_name);
            addressAdd = itemView.findViewById(R.id.txt_add_address);

        }
    }
}
