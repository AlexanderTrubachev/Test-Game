package com.company;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Main {

    public static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    public static void main(String[] args) {
	    WelcomeMenu welcomeMenu = WelcomeMenu.getInstance();
	    welcomeMenu.start();
    }
}
