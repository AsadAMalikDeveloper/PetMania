package com.example.petmania.model;

public class Branches {
    int id,clinic_id;
    String br_code,br_city,br_address,br_open,br_close,br_add_lat,br_add_lng,error_msg;

    public Branches() {
    }

    public Branches(int id, int clinic_id, String br_code, String br_city, String br_address, String br_open, String br_close, String br_add_lat, String br_add_lng,String error_msg) {
        this.id = id;
        this.clinic_id = clinic_id;
        this.br_code = br_code;
        this.br_city = br_city;
        this.br_address = br_address;
        this.br_open = br_open;
        this.br_close = br_close;
        this.br_add_lat = br_add_lat;
        this.br_add_lng = br_add_lng;
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

    public int getClinic_id() {
        return clinic_id;
    }

    public void setClinic_id(int clinic_id) {
        this.clinic_id = clinic_id;
    }

    public String getBr_code() {
        return br_code;
    }

    public void setBr_code(String br_code) {
        this.br_code = br_code;
    }

    public String getBr_city() {
        return br_city;
    }

    public void setBr_city(String br_city) {
        this.br_city = br_city;
    }

    public String getBr_address() {
        return br_address;
    }

    public void setBr_address(String br_address) {
        this.br_address = br_address;
    }

    public String getBr_open() {
        return br_open;
    }

    public void setBr_open(String br_open) {
        this.br_open = br_open;
    }

    public String getBr_close() {
        return br_close;
    }

    public void setBr_close(String br_close) {
        this.br_close = br_close;
    }

    public String getBr_add_lat() {
        return br_add_lat;
    }

    public void setBr_add_lat(String br_add_lat) {
        this.br_add_lat = br_add_lat;
    }

    public String getBr_add_lng() {
        return br_add_lng;
    }

    public void setBr_add_lng(String br_add_lng) {
        this.br_add_lng = br_add_lng;
    }
}
