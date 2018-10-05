package com.example.thita.fabapp;

public class ImageUploadInfo {
    public String imageName;
    public String imageURL;

    //constructor
    public ImageUploadInfo() {
    }

    //parameter constructor
    public ImageUploadInfo(String imgName , String imgURL){
        if (imgName.trim().equals("")){
            imgName = "no name";
        }
        this.imageName = imgName;
        this.imageURL = imgURL;
    }

    public String getImageName(){
        return imageName;
    }
    public String getImageURL (){
        return imageURL;
    }

    public void  setImageName(String name){
        imageName = name;
    }

    public void  setImageURL(String url){
        imageURL = url;
    }
}
