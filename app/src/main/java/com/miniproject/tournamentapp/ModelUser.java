package com.miniproject.tournamentapp;

public class ModelUser {

    final private String name,userid;
    final private int avatar;
    private boolean delete;

    public ModelUser(String name, String userid, int avatar,boolean delete) {
        this.name = name;
        this.userid = userid;
        this.avatar = avatar;
        this.delete = delete;
    }

    public String getName() {
        return name;
    }

    public String getUserid() {
        return userid;
    }

    public int getAvatar() {
        return avatar;
    }

    public boolean isDelete() {
        return delete;
    }
}
