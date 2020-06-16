package com.example.petmania.model;

public class Review {
    int id,user_id,dr_id;
    String rating,review,timestamp,error_msg;

    public Review(int id, int user_id, int dr_id, String rating, String review, String timestamp,String error_msg) {
        this.id = id;
        this.user_id = user_id;
        this.dr_id = dr_id;
        this.rating = rating;
        this.review = review;
        this.timestamp = timestamp;
        this.error_msg = error_msg;
    }

    public String getError_msg() {
        return error_msg;
    }

    public void setError_msg(String error_msg) {
        this.error_msg = error_msg;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getDr_id() {
        return dr_id;
    }

    public void setDr_id(int dr_id) {
        this.dr_id = dr_id;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
