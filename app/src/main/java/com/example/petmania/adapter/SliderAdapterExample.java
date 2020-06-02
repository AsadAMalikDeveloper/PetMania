package com.example.petmania.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.petmania.R;
import com.example.petmania.model.Adds_images;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.List;

public class SliderAdapterExample extends SliderViewAdapter<SliderAdapterExample.SliderAdapterVH> {

    private Context context;
    public static List<Adds_images> adds_imagesList;

    public SliderAdapterExample(Context context, List<Adds_images> adds_imagesList) {
        this.context = context;
        this.adds_imagesList = adds_imagesList;
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_slider_layout_item, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, int position) {
        viewHolder.textViewDescription.setText("This is item " + (position+1));

        for (int i=0;i<=adds_imagesList.size();i++) {
            Glide.with(viewHolder.itemView)
                    .load("https://petsmaniapk.000webhostapp.com/functions/images/uploads/".concat(adds_imagesList.get(position).getImage_url()))
                    .apply(new RequestOptions().placeholder(R.drawable.load))
                    .into(viewHolder.imageViewBackground);
        }

    }

    @Override
    public int getCount() {
        //slider view count could be dynamic size
        return adds_imagesList.size();
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        View itemView;
        ImageView imageViewBackground;
        TextView textViewDescription;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.iv_auto_image_slider);
            textViewDescription = itemView.findViewById(R.id.tv_auto_image_slider);
            this.itemView = itemView;
        }
    }
}
