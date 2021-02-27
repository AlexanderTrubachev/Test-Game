package com.company;

import java.io.*;
import java.util.ArrayList;


public class WelcomeMenu implements Serializable {
    private static WelcomeMenu instance;
    ArrayList<Player> playersList = new ArrayList<>();

    private WelcomeMenu() {}

    public static WelcomeMenu getInstance() {
        if(instance == null){
            instance = new WelcomeMenu();
        }
        return instance;
    }

    public void start() {
        try {
            loadState();
            System.out.println("Welcome to Tic Tac Toe game. \nTo play the game you need to create an account or use already existing. \nYou can see available commands by typing \"help\"");
            welcomeMenu();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void welcomeMenu() throws IOException {
        String input;

        System.out.println("Welcome to main menu \nEnter command");

        while (true) {
                input = Main.reader.readLine();

            switch (input) {
                case "help": {
                    System.out.println("help - show available commands \ncr- create new user \nlogin - login into account \nlist - get all players \ntop - top 10 players \ndel -delete account \nexit - close app");
                    continue;
                }

                case "cr": {
                    createUser();
                }

                case "login": {
                    Player player = login();
                    if(player != null) {
                        loginMenu(player);
                    } else {
                        welcomeMenu();
                    }
                }

                case "list": {
                    if (playersList.size() == 0){
                        System.out.println("No players registered");
                        welcomeMenu();
                    }

                    for (Player x: playersList) {
                        System.out.println(x.getPlayerName());
                    }
                    System.out.println("-----------------------------");
                    welcomeMenu();
                }

                case "top": {
                    top10();
                }

                case "del": {
                    deleteAccount();
                }

                case "exit": {
                    saveState();
                    Main.reader.close();
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
            name = Main.reader.readLine();

            for(Player x: playersList) {
                if(name.equals(x.getPlayerName())) {
                    System.out.println("Such username already exists. Try another.");
                    createUser();
                }
            }

            System.out.println("Enter preferred password");
            password = Main.reader.readLine();

            playersList.add(new Player(name, password));
            System.out.println("User created");
            welcomeMenu();
        }
    }

    private Player login() throws IOException{
        String name;
        String password;

        Player player = null;

        System.out.println("Enter username");
        name = Main.reader.readLine();

        for(Player x: WelcomeMenu.getInstance().playersList) {
            if(name.equals(x.getPlayerName())){
                player = x;
                break;
            }
        }

        if(player == null) {
            System.out.println("Such user doesn't exist");
            return null;
        }

        System.out.println("Enter password");
        password = Main.reader.readLine();

        if(player.passwordCheck(password)){
            System.out.println("Login complete! Welcome " + name + "!");
            return player;

        } else {
            System.out.println("Wrong password");
            return null;
        }
    }

    private void top10() throws IOException{
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
        welcomeMenu();
    }

    private void deleteAccount() throws IOException {
        System.out.println("Enter account name to delete account:");

        String deleteInput = Main.reader.readLine();

        for(Player x: playersList){
            if(x.getPlayerName().equals(deleteInput)) {
                System.out.println("Enter password");
                deleteInput = Main.reader.readLine();
                if (x.passwordCheck(deleteInput)) {
                    playersList.remove(x);
                    System.out.println("Account deleted");
                    welcomeMenu();
                } else {
                    System.out.println("Wrong password");
                    welcomeMenu();
                }
            }
        }

        System.out.println("Account not found");
        welcomeMenu();
    }

    private void loginMenu(Player player) throws IOException {
        System.out.println("Available commands: \nstart - launch the game \nstats - show stats \nup - update account data \ngamelogs - show list of played games \nshow game - show game log \nlogoff - return to main menu");

        String loginInput = Main.reader.readLine();

        switch (loginInput) {
            case "start": {
                startGame(player);
            }
            case "stats": {
                System.out.println(player.toString());
                loginMenu(player);
            }
            case "up": {
                updateAcc(player);
                loginMenu(player);
            }
            case "gamelogs": {
                player.showGamesID();
                loginMenu(player);
            }
            case "show game": {
                System.out.println("Enter game id");
                String inputID = Main.reader.readLine();
                try {
                    player.showGameLog(Integer.parseInt(inputID));
                } catch (NumberFormatException e) {
                    System.out.println("Incorrect input");
                } finally {
                    loginMenu(player);
                }
            }
            case "logoff": {
                System.out.println("Logoff complete");
                welcomeMenu();
            }
            default: {
                System.out.println("Command not found. Try again");
                loginMenu(player);
            }
        }
    }

    private void updateAcc(Player player) throws IOException{
        System.out.println("Type \"name\" to change name or \"password\" to change password. If you dont want to change anything - type exit.");

        String updateInput = Main.reader.readLine();

        switch (updateInput) {
            case "name": {
                System.out.println("Enter new name:");
                updateInput = Main.reader.readLine();
                player.setPlayerName(updateInput);
                System.out.println("Update successful");
                loginMenu(player);
            }
            case "password": {
                System.out.println("Enter new password:");
                updateInput = Main.reader.readLine();
                player.setPlayerPassword(updateInput);
                System.out.println("Update successful");
                loginMenu(player);
            }
            case "exit": {
                loginMenu(player);
            }
            default: {
                System.out.println("Command not found. Try again");
                updateAcc(player);
            }
        }
    }

    private void startGame(Player player) throws IOException {
        String input;
        Player player2 = null;

        System.out.println("Available game modes: \n 1.Player vs Player \n 2.Player vs AI \nType number to choose the game mode");
        input = Main.reader.readLine();

        if(!input.equals("1") && !input.equals("2")) {
            System.out.println("Invalid input");
            startGame(player);
        } else if(input.equals("1")) {
            GameController.getInstance().setGameMode(1);
            System.out.println("Player 2 login");
            player2 = login();

            if(player.equals(player2)) {
                System.out.println("Login declined. You cant play with yourself!");
                startGame(player);
            }

            if(player2 == null){
                GameController.getInstance().setGameMode(2);
                startGame(player);
            }
        }

        GameController game = GameController.getInstance();
        game.setPlayer1(player);
        game.setPlayer2(player2);
        game.startGame();
    }

    public void saveState() {
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("state.sav"));
            objectOutputStream.writeObject(playersList);
            objectOutputStream.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public void loadState() {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream("state.sav"));
            playersList = (ArrayList<Player>) objectInputStream.readObject();
            objectInputStream.close();
        } catch (FileNotFoundException e) {
        } catch (IOException exception) {
            exception.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
