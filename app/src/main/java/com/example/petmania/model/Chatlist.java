package com.example.petmania.model;

public class Chatlist {

    public  int id;
    public int ad_id;

    public Chatlist(int id, int ad_id) {
        this.id = id;
        this.ad_id = ad_id;
    }

    public Chatlist() {
    }

    public int getAd_id() {
        return ad_id;
    }

    public void setAd_id(int ad_id) {
        this.ad_id = ad_id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
