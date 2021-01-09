package com.example.infinitycrop.ui.Foro.lets_start.rv_community;

public class CommunityModel {

    //IMPORTANTE : los nombres tienen que ser iguales a los del documento del firebase

    private String img;
    private String name;
    private String description;
    private String creator;
    private int followers;
    private Boolean following;
    private int posts;

    //IMPORTANTE: tiene que haber un constructor vacío, si no, no funcionaría
    public CommunityModel() {}

    //Constructor que usare para añadir una communidad

    public CommunityModel(String img, String name, String description, String creator, int followers, Boolean following, int posts) {
        this.img = img;
        this.name = name;
        this.description = description;
        this.creator = creator;
        this.followers = followers;
        this.following = following;
        this.posts = posts;
    }




    /*        GETTERS & SETTERS            */

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public int getFollowers() {
        return followers;
    }

    public void setFollowers(int followers) {
        this.followers = followers;
    }

    public Boolean getFollowing() {
        return following;
    }

    public void setFollowing(Boolean following) {
        this.following = following;
    }

    public int getPosts() {
        return posts;
    }

    public void setPosts(int posts) {
        this.posts = posts;
    }
}
