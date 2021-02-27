package com.company;

import java.io.Serializable;
import java.util.ArrayList;

public class Player implements Serializable {
    private int id;
    private String name;
    private String password;

    private int gamesTotal = 0;
    private int gamesWon = 0;

    ArrayList<GameLog> gameLogs = new ArrayList<>();

    public Player(String name, String password) {
        id = (int) (Math.random() * 10000);
        this.name = name;
        this.password = password;
    }

    public int getPlayerID() {
        return id;
    }

    public String getPlayerName() {
        return name;
    }

    public void setPlayerName(String name) {this.name = name;}

    public void setPlayerPassword(String password) {this.password = password;}

    public boolean passwordCheck(String password) {
        if(this.password.equals(password)) { return true; }
        else {return false;}
    }

    @Override
    public String toString() {
        return "Player " + name + " stats: \n Games played: " + gamesTotal + "\n Games Won: " + gamesWon;
    }

    public void showGamesID() {
        int counter = 0;
        for(GameLog log: gameLogs){
            System.out.println(counter + ". " + log.getGameID());
            counter++;
        }
    }

    public void showGameLog(int id) {
       for(GameLog x: gameLogs) {
           if(x.getGameID() == id) {
               x.printGameLog();
               return;
           }
       }
       System.out.println("Game log not found");
    }

    public void gamesTotalInc() { this.gamesTotal++; }

    public int getGamesTotal() {
        return gamesTotal;
    }

    public void gamesWonInc() { this.gamesWon++; }

    public int getGamesWon() {
        return gamesWon;
    }

}