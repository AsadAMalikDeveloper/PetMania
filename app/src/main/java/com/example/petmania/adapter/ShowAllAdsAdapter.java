package com.example.petmania.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.petmania.R;
import com.example.petmania.activities.AdsDetailsActivity;
import com.example.petmania.activities.MainActivity;
import com.example.petmania.callback.IRecyclerClickListener;
import com.example.petmania.database.modeldb.Favourite;
import com.example.petmania.eventbus.AdsItemClick;
import com.example.petmania.model.Adds;
import com.example.petmania.utils.Common;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class ShowAllAdsAdapter extends RecyclerView.Adapter<ShowAllAdsAdapter.ViewHolder> {

    private Context context;
    private List<Adds> addsList;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    public ShowAllAdsAdapter(Context context, List<Adds> addsList) {
        this.context = context;
        this.addsList = addsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.all_ads_layout,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setListener(new IRecyclerClickListener() {
            @Override
            public void onItemClickListener(View view, int pos) {
                Common.SelectedAdd = addsList.get(pos);
                context.startActivity(new Intent(context, AdsDetailsActivity.class));
            }
        });

        if (Common.favouriteRespository.isFav(Integer.parseInt(String.valueOf(addsList.get(position).getAdds_id())))==1){
            holder.addToFav.setImageResource(R.drawable.ic_favorite_red_full_24dp);
        }else{
            holder.addToFav.setImageResource(R.drawable.ic_favorite_border_black_24dp);
        }

        holder.addToFav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.favouriteRespository.isFav(Integer.parseInt(String.valueOf(addsList.get(position).getAdds_id())))!=1){
                    addorRemoveFav(addsList.get(position),true);
                    holder.addToFav.setImageResource(R.drawable.ic_favorite_red_full_24dp);
                }else{
                    addorRemoveFav(addsList.get(position),false);
                    holder.addToFav.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                }
            }
        });

        holder.city.setText(addsList.get(position).getAd_city());
        compositeDisposable.add(Common.getAPI().getImages(addsList.get(position).getAdds_id())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(adds_images -> {
                    Glide.with(context).load("https://petsmaniapk.000webhostapp.com/functions/images/uploads/".concat(adds_images.get(position).getImage_url())).apply(new RequestOptions().placeholder(R.drawable.load)).into(holder.img);

                }));
        holder.title.setText(addsList.get(position).getAdd_title());
        holder.price.setText(new StringBuilder(addsList.get(position).getAdd_price()).append(" Rs."));
        holder.desc.setText(addsList.get(position).getAdd_detail());
    }

    private void addorRemoveFav(Adds adds, boolean isAdd) {
        Favourite favourite = new Favourite();
        favourite.ad_id = String.valueOf(adds.getAdds_id());
        favourite.title = adds.getAdd_title();
        favourite.price = adds.getAdd_price();

        favourite.location = String.valueOf(adds.getAddress_id());

        if (isAdd){
            Common.favouriteRespository.insertFav(favourite);
            Toast.makeText(context, "Added Successfully !", Toast.LENGTH_SHORT).show();
        }else{
            Common.favouriteRespository.delete(favourite);
            Toast.makeText(context, "Removed", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return addsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView img,addToFav;
        private TextView title,desc,price,city;
        private IRecyclerClickListener listener;

        public void setListener(IRecyclerClickListener listener) {
            this.listener = listener;
        }

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.category_title);
            desc = itemView.findViewById(R.id.hs_product_detail);
            price = itemView.findViewById(R.id.hs_product_price);
            city =  itemView.findViewById(R.id.hs_product_city);
            img=  itemView.findViewById(R.id.category_Image);
            addToFav = itemView.findViewById(R.id.add_to_fav_icon);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            listener.onItemClickListener(v,getAdapterPosition());
        }
    }
}
