package com.company;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class GameLog implements Serializable {
    int id;
    Date date;
    String gameRes;

    ArrayList<String> gameLog = new ArrayList<>();

    public void setGameRes(String gameRes) {
        this.gameRes = gameRes;
    }

    public int getGameID() {
        return id;
    }

    public GameLog() {
        id = (int) (Math.random() * 10000);
        date = new Date();
    }

    public void printGameLog() {
        System.out.println("Game Date: " + date + "\nGame ID: " + id + "\nGame result: "+ gameRes + "\n\nGame log: ");
        for(String line: gameLog) {
            System.out.println(line);
        }
    }
}
