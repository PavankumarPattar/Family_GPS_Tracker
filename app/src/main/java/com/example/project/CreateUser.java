package com.example.project;

public class CreateUser {


    public CreateUser() {
    }

    public CreateUser(String name2, String email2, String password2, String date2, String circlecode2, String userid2, String issharing2, String lat2, String lng2, String profile_image2) {
        this.name = name2;
        this.email = email2;
        this.password = password2;
        this.date = date2;
        this.circlecode = circlecode2;
        this.userid = userid2;
        this.issharing = issharing2;
        this.lat = lat2;
        this.lng = lng2;
        this.profile_image = profile_image2;
    }

    public String circlecode;
    public String date;
    public String email;
    public String issharing;
    public String lat;
    public String lng;
    public String name;
    public String password;
    public String profile_image;
    public String userid;
}
