package com.example.mohit.xmohit;

/**
 * Created by mohit on 17/3/17.
 */


public class ImageUpload {
    public String name;
    public String url;
    public String description;



    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getDescription() {
        return description;
    }


    public ImageUpload(String name,String url,String description){
        this.name = name;
        this.url = url;
        this.description = description;
    }

    public ImageUpload(){

    }
}