package com.example.petmania.model;

public class Adds {
    private int adds_id,category_id,user_id,published,address_id,show_no,views;
    private String add_title,add_detail,add_price,add_on,error_msg,expire_on,ad_city;

    public Adds(int adds_id, int category_id, int user_id, int published, int address_id, int show_no, int views, String add_title, String add_detail, String add_price, String add_on, String error_msg, String expire_on,String ad_city) {
        this.adds_id = adds_id;
        this.category_id = category_id;
        this.user_id = user_id;
        this.published = published;
        this.address_id = address_id;
        this.show_no = show_no;
        this.views = views;
        this.add_title = add_title;
        this.add_detail = add_detail;
        this.add_price = add_price;
        this.add_on = add_on;
        this.error_msg = error_msg;
        this.expire_on = expire_on;
        this.ad_city = ad_city;
    }

    public String getAd_city() {
        return ad_city;
    }

    public void setAd_city(String ad_city) {
        this.ad_city = ad_city;
    }

    public String getExpire_on() {
        return expire_on;
    }

    public void setExpire_on(String expire_on) {
        this.expire_on = expire_on;
    }

    public int getShow_no() {
        return show_no;
    }

    public void setShow_no(int show_no) {
        this.show_no = show_no;
    }

    public int getViews() {
        return views;
    }

    public void setViews(int views) {
        this.views = views;
    }

    public int getAdds_id() {
        return adds_id;
    }

    public void setAdds_id(int adds_id) {
        this.adds_id = adds_id;
    }

    public int getCategory_id() {
        return category_id;
    }

    public void setCategory_id(int category_id) {
        this.category_id = category_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getPublished() {
        return published;
    }

    public void setPublished(int published) {
        this.published = published;
    }

    public String getAdd_title() {
        return add_title;
    }

    public void setAdd_title(String add_title) {
        this.add_title = add_title;
    }

    public String getAdd_detail() {
        return add_detail;
    }

    public void setAdd_detail(String add_detail) {
        this.add_detail = add_detail;
    }

    public String getAdd_price() {
        return add_price;
    }

    public void setAdd_price(String add_price) {
        this.add_price = add_price;
    }

    public String getAdd_on() {
        return add_on;
    }

    public void setAdd_on(String add_on) {
        this.add_on = add_on;
    }

    public String getError_msg() {
        return error_msg;
    }

    public void setError_msg(String error_msg) {
        this.error_msg = error_msg;
    }

    public int getAddress_id() {
        return address_id;
    }

    public void setAddress_id(int address_id) {
        this.address_id = address_id;
    }
}
