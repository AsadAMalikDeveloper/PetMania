package com.example.petmania.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.petmania.R;
import com.example.petmania.callback.IRecyclerClickListener;
import com.example.petmania.database.local.PetManiaRoomDatabase;
import com.example.petmania.database.modeldb.Favourite;
import com.example.petmania.model.Adds;
import com.example.petmania.utils.Common;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import dmax.dialog.SpotsDialog;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class FavouriteListAdapter extends RecyclerView.Adapter<FavouriteListAdapter.ViewHolder> {

    Context context;
    List<Favourite> favouriteList;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    public FavouriteListAdapter(Context context, List<Favourite> favouriteList) {
        this.context = context;
        this.favouriteList = favouriteList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.fav_item_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setListener(new IRecyclerClickListener() {
            @Override
            public void onItemClickListener(View view, int pos) {
            }
        });
        compositeDisposable.add(Common.getAPI().loadAddressWithAddId(Integer.parseInt(favouriteList.get(position).location))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(addresses -> {
                    holder.city.setText(new StringBuilder(addresses.get(position).getCity()).append(", ").append(addresses.get(position).getCountry()));
                }));
        compositeDisposable.add(Common.getAPI().getImages(Integer.parseInt(favouriteList.get(position).ad_id))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(adds_images -> {
                    Glide.with(context).load("https://petsmaniapk.000webhostapp.com/functions/images/uploads/".concat(adds_images.get(position).getImage_url())).apply(new RequestOptions().placeholder(R.drawable.load)).into(holder.img);

                }));
        holder.title.setText(favouriteList.get(position).title);
        holder.price.setText(new StringBuilder(favouriteList.get(position).price).append(" Rs."));
    }

    @Override
    public int getItemCount() {
        return favouriteList.size();
    }

    public void removeItem(int deleteIndex) {
        favouriteList.remove(deleteIndex);
        notifyItemRemoved(deleteIndex);
    }
    public void restoreItem(Favourite item,int position){
        favouriteList.add(position,item);
        notifyItemInserted(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView img;
        private TextView title,price,city;
        private IRecyclerClickListener listener;
        public RelativeLayout view_backgroung;
        public ConstraintLayout view_foreground;

        public void setListener(IRecyclerClickListener listener) {
            this.listener = listener;
        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.category_title);
            price = itemView.findViewById(R.id.hs_product_price);
            img=  itemView.findViewById(R.id.category_Image);
            city =  itemView.findViewById(R.id.hs_product_city);
            view_backgroung = itemView.findViewById(R.id.view_background);
            view_foreground = itemView.findViewById(R.id.view_foreground);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onItemClickListener(v,getAdapterPosition());
        }
    }
}
