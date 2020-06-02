package com.example.petmania.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petmania.R;
import com.example.petmania.activities.AddressActivity;
import com.example.petmania.activities.MainActivity;
import com.example.petmania.interfaces.ItemClickListener;
import com.example.petmania.model.Addresses;
import com.example.petmania.prevalent.Prevalent;
import com.example.petmania.utils.Common;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;
import io.reactivex.functions.Consumer;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    List<Addresses> addresses;
    Context context;
    public static int selectedId = 0;
    private static int row_index = -1;

    public AddressAdapter(List<Addresses> addresses, Context context) {
        this.addresses = addresses;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.address_item_layout,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.name.setText(addresses.get(position).getCity());
        holder.address.setText(addresses.get(position).getAddress());

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                Common.currentAddres =  addresses.get(position);
                Paper.book().write(Prevalent.address,addresses.get(position));
                row_index= position;
                notifyDataSetChanged();
            }
        });

        if (row_index!=position){

            holder.check.setVisibility(View.GONE);
            holder.itemView.setBackgroundColor(Color.parseColor("#ffffff"));
        }else{
            holder.check.setVisibility(View.VISIBLE);
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.successGreen));
        }

    }

    @Override
    public int getItemCount() {
        return addresses.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView name,address;
        RelativeLayout relativeLayout;
        ItemClickListener itemClickListener;
        ImageView check;
        public void setItemClickListener(ItemClickListener itemClickListener){
            this.itemClickListener= itemClickListener;
        }
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.address_userName);
            address = itemView.findViewById(R.id.address_userAddress);
            relativeLayout = itemView.findViewById(R.id.address_rl);
            check = itemView.findViewById(R.id.check_img);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onClick(v,getAdapterPosition());
        }
    }
}
