package com.example.petmania.model;

public class Addresses {
    private String country,city,state,postal_code,error_msg,address;
    private int user_id,address_id;

    public Addresses() {
    }

    public Addresses(String country, String city, String state, String postal_code, String error_msg, String address, int user_id, int address_id) {
        this.country = country;
        this.city = city;
        this.state = state;
        this.postal_code = postal_code;
        this.error_msg = error_msg;
        this.address = address;
        this.user_id = user_id;
        this.address_id = address_id;
    }

    public String getError_msg() {
        return error_msg;
    }

    public void setError_msg(String error_msg) {
        this.error_msg = error_msg;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostal_code() {
        return postal_code;
    }

    public void setPostal_code(String postal_code) {
        this.postal_code = postal_code;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public int getAddress_id() {
        return address_id;
    }

    public void setAddress_id(int address_id) {
        this.address_id = address_id;
    }
}
