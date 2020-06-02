package com.example.petmania.model;

import java.io.Serializable;

public class Doctors  implements Serializable {
    private int id,br_id,dr_show_no;
    private String dr_email,dr_name,dr_phone,dr_pass,dr_desc,dr_speciality,dr_fee;

    public Doctors() {
    }

    public Doctors(int id, int br_id, int dr_show_no, String dr_email, String dr_name, String dr_phone, String dr_pass, String dr_desc, String dr_speciality, String dr_fee) {
        this.id = id;
        this.br_id = br_id;
        this.dr_show_no = dr_show_no;
        this.dr_email = dr_email;
        this.dr_name = dr_name;
        this.dr_phone = dr_phone;
        this.dr_pass = dr_pass;
        this.dr_desc = dr_desc;
        this.dr_speciality = dr_speciality;
        this.dr_fee = dr_fee;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getBr_id() {
        return br_id;
    }

    public void setBr_id(int br_id) {
        this.br_id = br_id;
    }

    public int getDr_show_no() {
        return dr_show_no;
    }

    public void setDr_show_no(int dr_show_no) {
        this.dr_show_no = dr_show_no;
    }

    public String getDr_email() {
        return dr_email;
    }

    public void setDr_email(String dr_email) {
        this.dr_email = dr_email;
    }

    public String getDr_name() {
        return dr_name;
    }

    public void setDr_name(String dr_name) {
        this.dr_name = dr_name;
    }

    public String getDr_phone() {
        return dr_phone;
    }

    public void setDr_phone(String dr_phone) {
        this.dr_phone = dr_phone;
    }

    public String getDr_pass() {
        return dr_pass;
    }

    public void setDr_pass(String dr_pass) {
        this.dr_pass = dr_pass;
    }

    public String getDr_desc() {
        return dr_desc;
    }

    public void setDr_desc(String dr_desc) {
        this.dr_desc = dr_desc;
    }

    public String getDr_speciality() {
        return dr_speciality;
    }

    public void setDr_speciality(String dr_speciality) {
        this.dr_speciality = dr_speciality;
    }

    public String getDr_fee() {
        return dr_fee;
    }

    public void setDr_fee(String dr_fee) {
        this.dr_fee = dr_fee;
    }
}
