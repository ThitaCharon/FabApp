package com.example.thita.fabapp;

public class ImageUploadInfo {
    public String imageName;
    public String imageURL;

    //constructor
    public ImageUploadInfo(){}
    //parameter constructor
    public ImageUploadInfo(String imgName , String imgURL){
        this.imageName = imgName;
        this.imageURL = imgURL;
    }

    public String getImageName(){
        return imageName;
    }

    public String getImageURL (){
        return imageURL;
    }


}
