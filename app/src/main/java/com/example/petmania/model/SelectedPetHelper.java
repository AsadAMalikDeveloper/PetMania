package com.example.petmania.model;

import android.graphics.drawable.GradientDrawable;

public class SelectedPetHelper {
    public String header,desc;

    GradientDrawable bg_clr;

    public SelectedPetHelper(String header, String desc,GradientDrawable bg_clr) {
        this.header = header;
        this.desc = desc;
        this.bg_clr = bg_clr;
    }

    public GradientDrawable getBg_clr() {
        return bg_clr;
    }

    public void setBg_clr(GradientDrawable bg_clr) {
        this.bg_clr = bg_clr;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
