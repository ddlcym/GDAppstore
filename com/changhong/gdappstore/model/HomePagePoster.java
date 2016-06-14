package com.changhong.gdappstore.model;

/**
 * Created by Yves Yang on 2016/3/22.
 */
public class HomePagePoster {
    int id;
    String picAddress;

    public HomePagePoster(int id, String picAddress) {
        this.id = id;
        this.picAddress = picAddress;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPicAddress() {
        return picAddress;
    }

    public void setPicAddress(String picAddress) {
        this.picAddress = picAddress;
    }

    @Override
    public String toString() {
        return "HomePagePoster{" +
                "id=" + id +
                ", picAddress='" + picAddress + '\'' +
                '}';
    }
}
