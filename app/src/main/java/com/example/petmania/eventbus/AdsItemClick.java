package com.example.petmania.eventbus;

import com.example.petmania.model.Adds;

public class AdsItemClick {

    private boolean success;
    private Adds addsModel;

    public AdsItemClick(boolean success, Adds addsModel) {
        this.success = success;
        this.addsModel = addsModel;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Adds getAddsModel() {
        return addsModel;
    }

    public void setAddsModel(Adds addsModel) {
        this.addsModel = addsModel;
    }
}
