package com.miniproject.tournamentapp;

public class ModelTournament {

    private String name,date,time,pJoined,prize,game,pType,id;

    public ModelTournament(String name, String date, String time, String pJoined, String prize, String game, String pType,String id) {
        this.name = name;
        this.date = date;
        this.time = time;
        this.pJoined = pJoined;
        this.prize = prize;
        this.pType = pType;
        this.game = game;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getpJoined() {
        return pJoined;
    }

    public String getPrize() {
        return prize;
    }

    public String getpType() {
        return pType;
    }

    public String getGame() {
        return game;
    }

    public String getId() {
        return id;
    }
}
