package com.miniproject.tournamentapp;

public class ModelInterests {

    private String gameName;
    private boolean selected;

    public ModelInterests(String gameName, boolean selected) {
        this.gameName = gameName;
        this.selected = selected;
    }

    public String getGameName() {
        return gameName;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
