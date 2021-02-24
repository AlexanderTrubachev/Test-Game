package com.company;

public class Main {

    public static void main(String[] args) {
	GameController game = GameController.getInstance();
	WelcomeMenu welcomeMenu = new WelcomeMenu();
	welcomeMenu.start();
	game.startGame();
    }
}
