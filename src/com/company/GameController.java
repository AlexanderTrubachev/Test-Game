package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class GameController {

    private static GameController instance;

    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    private char[][] gameField = new char[3][3];        //игровое поле 3х3
    private int gameMode = 2;
    private int emptyField = 9;
    private boolean endGame = false;
    private String player1name = "Player 1";
    private String player2name = "Player 2";

    private GameController() {}

    public static GameController getInstance() {            //потокобезопасный синглтон
        synchronized (GameController.class){
            if(instance == null){
                instance = new GameController();
            }
            return instance;
        }
    }

    public void setPlayer1name(String name){
        this.player1name = name;
    }

    public void setPlayer2name(String name){
        this.player2name = name;
    }

    public String getPlayer1name() {
        return  player1name;
    }

    public String getPlayer2name() {
        return  player2name;
    }

    public void startGame(){                          //Публичный метод, запускающий игровой процесс
        initGameField();
        chooseGameMode();
        actionPhase();
        try {
            reader.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    private void initGameField() {                  //инициализация массива gameField пробелами для корректного
        for(int a = 0; a < 3; a++){                 //отображения ячеек игрового поля относительно координат
            for (int b = 0; b < 3; b++){
                gameField[a][b] = ' ';
            }
        }
    }

    private void drawField() {                     //Метод, отрисовывающий игровое поле
        System.out.println("    0   1   2   ");
        System.out.printf("A   %s   %s   %s   \n", gameField[0][0], gameField[1][0], gameField[2][0]);
        System.out.printf("B   %s   %s   %s   \n", gameField[0][1], gameField[1][1], gameField[2][1]);
        System.out.printf("C   %s   %s   %s   \n", gameField[0][2], gameField[1][2], gameField[2][2]);

    }

    private void chooseGameMode() {                      // Выбор режима игры: против игрока или против ИИ.
                                                         // По умолчанию выставлен режим против ИИ.

        System.out.println("Available game modes: \n 1.Player vs Player \n 2.Player vs AI \n Type number to choose the game mode");

        try{
            gameMode = Integer.parseInt(reader.readLine());     //! переделать под switch!
            if(gameMode != 1 && gameMode != 2) {                // Если gameMode не равен ожидаемому значению, то
                gameMode = 2;                                   // выбрасывается исключение. Значение gameMode
                throw new IOException();                        // устанавливается по умолчанию. Процедура ввода
            }                                                   // повторяется пока игра не получит ожидаемое значение, после
                                                                // чего поток закрывается
        } catch (IOException | NumberFormatException exception) {
            System.out.println("Incorrect Input");
            chooseGameMode();
        }

    }

    private void actionPhase() {               // Активная фаза игры. Ввод игроками координат

        boolean flag = true;                   // flag - true: ход 1 игрока, flag - false: ход 2 игрока

        while (!endGame){
            int x;
            int y;

            drawField();
            endGameConditionsCheck();

            if(endGame) {
                System.out.println("Game Over!");
                if(emptyField == 0) { System.out.println("Draw! Noone wins!");}
                else if(!flag) {System.out.println(player1name + " wins!");}
                else {System.out.println(player2name +" wins!");}
                break;
            }

            if(flag) System.out.println(player1name + " turn!");
            else System.out.println(player2name + " turn!");

            System.out.println("Enter cell coordinate(ex. A 0):");

            try {
                String input = reader.readLine().toUpperCase();

                String[] coordinates;
                coordinates = input.split(" ");

                if(coordinates[0].matches("[0-9]")){                // При неверном формате ввода координат
                    String buffer = coordinates[0];                      // происходит смена значений элементов массива
                    coordinates[0] = coordinates[1];                     // местами
                    coordinates[1] = buffer;
                }

                if(coordinates[0].equals("A")){ coordinates[0] = "0";}      // замена буквенных координат на соотвестующий
                if(coordinates[0].equals("B")){ coordinates[0] = "1";}      // номер
                if(coordinates[0].equals("C")){ coordinates[0] = "2";}

                x = Integer.parseInt(coordinates[1]);
                y = Integer.parseInt(coordinates[0]);

                if(gameField[x][y] ==' '){                                  // Если поле не занято, происходит ввод
                    if(flag) {gameField[x][y] = 'X';}                       // соответствующего символа, иначе выброс
                    else {gameField[x][y] = 'O';}                           // исключения и вызов метода continue
                    flag =! flag;
                } else {
                    System.out.println("Cell occupied!");
                    throw new IOException();
                }
            } catch (ArrayIndexOutOfBoundsException | NumberFormatException | IOException exception) {
                System.out.println("Incorrect input");
            }
        }
    }

    private void endGameConditionsCheck() {      // Вариант реализации: проверка по каждому варианту завершения игры (9 исходов: 8 побед, 1 ничья)

        emptyField = 9;

        for (int a = 0; a < 3; a++){            // проверка по столбцам
            if(gameField[a][0] != ' ' && gameField[a][0] == gameField[a][1] && gameField[a][0] == gameField[a][2]){
                endGame = true;
                break;
            }
        }

        for (int a = 0; a < 3; a++){            // проверка по строкам
            if (gameField[0][a] != ' ' && gameField[0][a] == gameField[1][a] && gameField[0][a] == gameField[2][a]) {
                endGame = true;
                break;
            }
        }

        // проверка по диагонали свреху слева до вниз справа
        if(gameField[0][0] != ' ' && gameField[0][0] == gameField[1][1] && gameField[0][0] == gameField[2][2]){
            endGame = true;
        }

        // проверка по диагонали снизу слева до сверху справа
        if(gameField[0][2] != ' ' && gameField[0][2] == gameField[1][1] && gameField[0][2] == gameField[2][0]){
            endGame = true;
        }

        for(int a = 0; a < 3; a++) {                    // проверка на ничью
            for(int b = 0; b < 3; b++) {
                if(gameField[a][b] != ' '){
                    emptyField--;
                    if(emptyField == 0) {
                        endGame = true;
                    }
                }
            }
        }
    }
}