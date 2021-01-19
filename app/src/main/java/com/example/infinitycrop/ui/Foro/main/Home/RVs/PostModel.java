package com.example.infinitycrop.ui.Foro.main.Home.RVs;

import com.google.firebase.Timestamp;

public class PostModel {
    private String creator;
    private String community;
    private String tittle;
    private String desc;
    private int likes;
    private String img;
    private Timestamp date;
    private String comments;
    private String uid;

    public PostModel() {
    }

    public PostModel(String creator, String community, String tittle, String desc, int likes, String img, Timestamp date, String comments, String uid) {
        this.creator = creator;
        this.community = community;
        this.tittle = tittle;
        this.desc = desc;
        this.likes = likes;
        this.img = img;
        this.date = date;
        this.comments = comments;
        this.uid = uid;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getCommunity() {
        return community;
    }

    public void setCommunity(String community) {
        this.community = community;
    }

    public String getTittle() {
        return tittle;
    }

    public void setTittle(String tittle) {
        this.tittle = tittle;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
