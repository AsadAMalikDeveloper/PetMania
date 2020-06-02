package com.example.petmania.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.Image;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.petmania.R;
import com.example.petmania.activities.DisplayActivity;
import com.example.petmania.model.Adds;
import com.example.petmania.model.Adds_images;
import com.example.petmania.prevalent.Prevalent;
import com.example.petmania.utils.Common;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import io.paperdb.Paper;

import static com.example.petmania.adapter.SliderAdapterExample.adds_imagesList;


public class AddsAdapter extends RecyclerView.Adapter<AddsAdapter.ViewHolder> {

    Context context;
    List<Adds> addsList;
    public static int adPosition =-1;

    public AddsAdapter(Context context, List<Adds> addsList) {
        this.context = context;
        this.addsList = addsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adds_layout,parent,false);

        return new ViewHolder(view);
    }
    //String name = new StringBuilder("https://petsmaniapk.000webhostapp.com/images/uploads/").append(adds_imagesList.get(position+i).getImage_url()).toString();

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        holder.name.setText(addsList.get(position).getAdd_title());
        if (addsList.get(position).getPublished()==0){
            adPosition = addsList.get(position).getAdds_id();
            holder.adImg.setVisibility(View.GONE);
            holder.from.setVisibility(View.GONE);
            holder.fromVal.setVisibility(View.GONE);
            holder.to.setVisibility(View.GONE);
            holder.toVal.setVisibility(View.GONE);
            holder.review.setTextColor(Color.RED);
            holder.review_icon.setVisibility(View.VISIBLE);
            holder.review.setText("Reviewing....");
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onClick(View v) {
                    showEndDialog();
                }
            });
        }else {

            adPosition = addsList.get(position).getAdds_id();
//            String name = "https://petsmaniapk.000webhostapp.com/functions/images/uploads/".concat(Common.currentUser.getPhone()).concat("_")
//                    .concat(String.valueOf(addsList.get(position).getAdds_id())).concat("_").concat(String.valueOf(0)).concat(".jpg");
//            Log.d("AddsAdapter", "onBindViewHolder: @@@!!! "+name);

            Glide.with(context)
                    .load("https://petsmaniapk.000webhostapp.com/functions/images/uploads/".concat(Common.currentUser.getPhone()).concat("_")
                            .concat(String.valueOf(addsList.get(position).getAdds_id())).concat("_").concat(String.valueOf(0)).concat(".jpg"))
                    .apply(new RequestOptions().placeholder(R.drawable.load))
                    .into(holder.adImg);
            holder.review_icon.setVisibility(View.GONE);
            holder.from.setVisibility(View.VISIBLE);
            holder.fromVal.setVisibility(View.VISIBLE);
            holder.to.setVisibility(View.VISIBLE);
            holder.toVal.setVisibility(View.VISIBLE);

            holder.fromVal.setText(addsList.get(position).getAdd_on());
            holder.toVal.setText(addsList.get(position).getExpire_on());
            holder.review.setTextColor(context.getResources().getColor(R.color.successGreen));
            holder.review.setText("Active");
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Paper.book().write(Prevalent.lastAdd,addsList.get(position));
                    Common.EditSelectedAd = addsList.get(position);
                    gotoDisplay(addsList.get(position).getAdds_id());
                }
            });
        }
        //String name = new StringBuilder("https://petsmaniapk.000webhostapp.com/images/uploads/").append(adds_imagesList.get(position).getImage_url()).toString();


    }

    private void gotoDisplay(int adds_id) {
        Intent intent = new Intent(context, DisplayActivity.class);
        context.startActivity(intent);
    }


    @Override
    public int getItemCount() {
        return addsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name,review,from,fromVal,to,toVal;
        ImageView review_icon;
        ImageView adImg;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.add_name);
            review = itemView.findViewById(R.id.add_review);
            adImg = itemView.findViewById(R.id.ad_img);
            review_icon = itemView.findViewById(R.id.review_icon);
            from = itemView.findViewById(R.id.from_tv);
            fromVal = itemView.findViewById(R.id.from_tv_val);
            to = itemView.findViewById(R.id.to_tv);
            toVal = itemView.findViewById(R.id.to_tv_val);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showEndDialog() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("REVIEWING ADDS");
        alertDialogBuilder
                .setMessage("Please wait we are checking your add you will shortly recieve mail when your add will be published")
                .setCancelable(false)
                .setView(R.layout.waiting_layout).setPositiveButton("Ok, Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
             dialog.dismiss();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}
