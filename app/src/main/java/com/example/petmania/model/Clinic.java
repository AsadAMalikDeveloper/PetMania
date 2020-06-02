package com.example.petmania.model;

public class Clinic {
    int id;
    String clinic_name;

    public Clinic() {
    }

    public Clinic(int id, String clinic_name) {
        this.id = id;
        this.clinic_name = clinic_name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClinic_name() {
        return clinic_name;
    }

    public void setClinic_name(String clinic_name) {
        this.clinic_name = clinic_name;
    }
}
