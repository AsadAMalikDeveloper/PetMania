package com.example.petmania.model;

public class Adds_images {
    int adds_id;
    String image_url,uploaded_on;


    public Adds_images(int adds_id, String image_url, String uploaded_on) {
        this.adds_id = adds_id;
        this.image_url = image_url;
        this.uploaded_on = uploaded_on;
    }


    public int getAdds_id() {
        return adds_id;
    }

    public void setAdds_id(int adds_id) {
        this.adds_id = adds_id;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getUploaded_on() {
        return uploaded_on;
    }

    public void setUploaded_on(String uploaded_on) {
        this.uploaded_on = uploaded_on;
    }
}
