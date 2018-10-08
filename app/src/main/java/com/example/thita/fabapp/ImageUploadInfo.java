package com.example.thita.fabapp;

import com.google.firebase.database.Exclude;

public class ImageUploadInfo {
    public String imageName;
    public String imageURL;
    public String imageQuantity;
    public String imageUnit;
    private String key;

    //constructor
    public ImageUploadInfo() {
    }

    //parameter constructor
    public ImageUploadInfo(String imgName , String imgURL, String quantity, String unit){
        if (imgName.trim().equals("")){
            imgName = "no name";
        }
        this.imageName = imgName;
        this.imageURL = imgURL;
        imageQuantity = quantity;
        imageUnit = unit;
    }


    public String getImageName(){
        return imageName;
    }
    public String getImageURL (){
        return imageURL;
    }
    public String getImageUnit(){
        return imageUnit;
    }
    public String getImageQuantity(){
        return imageQuantity;
    }
    @Exclude
    public String getKey(){
        return key;
    }
    public void  setImageName(String name){
        imageName = name;
    }
    public void  setImageURL(String url){
        imageURL = url;
    }
    public void  setImageUnit(String unit){
        imageUnit = unit;
    }
    public void  setImageQuantity(String quantity){
        imageQuantity = quantity;
    }
    @Exclude
    public void  setKey(String key){
        this.key = key;
    }
}
