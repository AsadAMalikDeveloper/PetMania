package com.example.petmania.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.viewpager.widget.PagerAdapter;

import com.example.petmania.R;
import com.example.petmania.model.Branches;
import com.example.petmania.model.Doctors;
import com.example.petmania.prevalent.Prevalent;
import com.example.petmania.utils.Common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.paperdb.Paper;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class ViewPagerAdapter extends PagerAdapter {
    private Context context;
    private ArrayList<Doctors> doctorsArrayList;
    private int br_id;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public ViewPagerAdapter(Context context, ArrayList<Doctors> doctorsArrayList) {
        this.context = context;
        this.doctorsArrayList = doctorsArrayList;
    }

    @Override
    public int getCount() {
        return doctorsArrayList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==(RelativeLayout)object;
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
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.pager_content,container,false);
        container.addView(view);
//        compositeDisposable.add(Common.getAPI().getAllBranches(br_id)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(branches -> {
//
//
//                }));
        Branches branches = Paper.book().read(Prevalent.SelectedBranch);
        String close = changeTime(branches.getBr_close());
        String open = changeTime(branches.getBr_open());
        TextView timing = view.findViewById(R.id.time_tv);
        timing.setText(new StringBuilder(open).append("-").append(close));
        TextView address = view.findViewById(R.id.address_tv);
        address.setElegantTextHeight(true);
        address.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        address.setSingleLine(false);
        address.setText(branches.getBr_address());
        TextView name = view.findViewById(R.id.dr_name);
        name.setText(doctorsArrayList.get(position).getDr_name());
        TextView spec = view.findViewById(R.id.dr_spec);
        spec.setText(doctorsArrayList.get(position).getDr_speciality());
        TextView fee = view.findViewById(R.id.fee_tv);
        fee.setText(new StringBuilder("Rs. ").append(doctorsArrayList.get(position).getDr_fee()).append("/Session"));
        TextView about = view.findViewById(R.id.about_tv);
        about.setElegantTextHeight(true);
        about.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        about.setSingleLine(false);
        about.setText(doctorsArrayList.get(position).getDr_desc());
        LinearLayout showNo,noShowNo;
        showNo = view.findViewById(R.id.footer_showno);
        noShowNo = view.findViewById(R.id.footer_Noshowno);
        if (doctorsArrayList.get(position).getDr_show_no()==0){
            showNo.setVisibility(View.GONE);
            noShowNo.setVisibility(View.VISIBLE);
            LinearLayout chatBtn,mapBtn;
            chatBtn = view.findViewById(R.id.chat_btn1);
            mapBtn = view.findViewById(R.id.map_btn1);

            chatBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Chat "+doctorsArrayList.get(position).getDr_name(), Toast.LENGTH_SHORT).show();
                }
            });
            mapBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
//                            Uri.parse("geo:"+branches.getBr_add_lat()+","+branches.getBr_add_lng()+""));
//                    Log.d("Adapter", "onClick: !!! "+Uri.parse("geo:"+branches.getBr_add_lat()+","+branches.getBr_add_lng()+""));
//                    context.startActivity(intent);
                    String strUri = "http://maps.google.com/maps?q=loc:" + branches.getBr_add_lat() + "," + branches.getBr_add_lng() + " (" + "Label which you want" + ")";
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(strUri));
                    intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");
                    context.startActivity(intent);

                }
            });
        }else {
            showNo.setVisibility(View.VISIBLE);
            noShowNo.setVisibility(View.GONE);
            LinearLayout chatBtn,callBtn,mapBtn;
            chatBtn = view.findViewById(R.id.chat_btn);
            callBtn = view.findViewById(R.id.call_btn);
            mapBtn = view.findViewById(R.id.map_btn);
            chatBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Chat "+doctorsArrayList.get(position).getDr_name(), Toast.LENGTH_SHORT).show();
                }
            });
            callBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", doctorsArrayList.get(position).getDr_phone(), null));
                        context.startActivity(intent);
                }
            });
            mapBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String strUri = "http://maps.google.com/maps?q=loc:" + branches.getBr_add_lat() + "," + branches.getBr_add_lng() + " (" + "Label which you want" + ")";
                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(strUri));

                    intent.setClassName("com.google.android.apps.maps", "com.google.android.maps.MapsActivity");

                    context.startActivity(intent);
                    }
            });
        }
        return view;

    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
