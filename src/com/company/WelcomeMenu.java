package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;


public class WelcomeMenu {
    ArrayList<Player> playersList = new ArrayList<>();

    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public void start() {
        try {
            welcomeMenu();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void welcomeMenu() throws IOException {
        String input;

        System.out.println("Welcome to Tic Tac Toe game. \nTo play the game you need to create an account or use already existing. \nYou can see available commands by typing \"help\"");

        while (true) {
                input = reader.readLine();

            switch (input) {
                case "help": {
                    System.out.println("help - show available commands \ncr- create new user \nlogin - login into account \nlist - get all players \ntop - top 10 players \nexit - close app");
                    continue;
                }

                case "cr": {
                    createUser();
                }

                case "login": {
                    login();
                }

                case "list": {
                    if (playersList.size() == 0){
                        System.out.println("No players registered");
                        welcomeMenu();
                    }

                    for (Player x: playersList) {
                        System.out.println(x.getPlayerName());
                    }
                    welcomeMenu();
                }

                case "top": {
                    top10();
                }

                case "exit": {
                    System.exit(0);
                }

                default: {
                    System.out.println("Command not found. Try again");
                    welcomeMenu();
                }
            }
        }
    }

    private void createUser() throws IOException {
        String name;
        String password;

        System.out.println("Enter preferred username");
        while (true) {
            name = reader.readLine();

            for(Player x: playersList) {
                if(name.equals(x.getPlayerName())) {
                    System.out.println("Such username already exists. Try another.");
                    createUser();
                }
            }

            System.out.println("Enter preferred password");
            password = reader.readLine();

            playersList.add(new Player(name, password));
            System.out.println("User created");
            welcomeMenu();
        }
    }

    private void login() throws IOException{            // Баг с повторным выводом имени при успешной авторизации
        String name;
        String password;

        Player player = null;

        System.out.println("Enter username");
        name = reader.readLine();

        for(Player x: playersList) {
            if(name.equals(x.getPlayerName())){
                player = x;
            } else {
                System.out.println("Such user doesn't exist");
                welcomeMenu();
            }
        }

        System.out.println("Enter password");
        password = reader.readLine();

        if(player.passwordCheck(password)){
            System.out.println("Login complete! Welcome " + name + "!");
        } else {
            System.out.println("Wrong password");
            welcomeMenu();
        }
    }

    private void top10() {
        for (int a = 0; a < playersList.size() - 1; a++){
            for (int b = 0; b < playersList.size() - a - 1; b++){
                if(playersList.get(b).getGamesTotal() < playersList.get(b + 1).getGamesTotal()){
                    Player temp = playersList.get(b);
                    playersList.set(b, playersList.get(b + 1));
                    playersList.set(b + 1, temp);
                }
            }
        }

        int limit = 10;
        if(playersList.size() < 10) {
            limit = playersList.size();
        }

        for(int a = 0; a < limit; a++){
            System.out.println((a + 1 + "." + playersList.get(a).toString()));
        }
    }
}
