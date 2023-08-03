package com.miniproject.tournamentapp;

public class ModelRequest {

    private String name,id;
    private int dp;

    public ModelRequest(String name, String id, int dp) {
        this.name = name;
        this.id = id;
        this.dp = dp;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public int getDp() {
        return dp;
    }
}
