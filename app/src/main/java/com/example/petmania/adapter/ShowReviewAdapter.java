package com.example.petmania.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petmania.R;
import com.example.petmania.model.Review;
import com.example.petmania.model.User;
import com.example.petmania.utils.Common;

import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShowReviewAdapter extends RecyclerView.Adapter<ShowReviewAdapter.ViewHolder> {
    private List<Review> reviewArrayList;
    private Context context;

    public ShowReviewAdapter(List<Review> reviewArrayList, Context context) {
        this.reviewArrayList = reviewArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_review_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Review review = reviewArrayList.get(position);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(review.getTimestamp()));
        String date = DateFormat.format("dd/MM/yyyy",calendar).toString();

        holder.date.setText(date);
        holder.ratingBar.setRating(Float.parseFloat(review.getRating()));
        holder.desc.setText(review.getReview());
        Common.getAPI().getUserByUserId(String.valueOf(reviewArrayList.get(position).getUser_id()))
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        User user = response.body();
                        if (TextUtils.isEmpty(user.getError_msg())){
                            holder.name.setText(user.getName());
                        }else {
                            holder.name.setText("User");
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Toast.makeText(context, "ERROR "+t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public int getItemCount() {
        return reviewArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView name,date,desc;
        private RatingBar ratingBar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name= itemView.findViewById(R.id.review_item_name);
            date = itemView.findViewById(R.id.review_item_date);
            desc = itemView.findViewById(R.id.review_item_reviewTv);
            ratingBar = itemView.findViewById(R.id.review_item_rating);
        }
    }
}
