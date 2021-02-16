package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class GameController {

    private static GameController instance;

    private char[][] gameField = new char[3][3];        //игровое поле 3х3
    private int gameMode = 2;
    private boolean endGame = false;

    private GameController() {}

    public static GameController getInstance() {            //потокобезопасный синглтон
        synchronized (GameController.class){
            if(instance == null){
                instance = new GameController();
            }
            return instance;
        }
    }

    public void startGame(){                          //Публичный метод, запускающий игровой процесс
        initGameField();
        chooseGameMode();
        drawField();
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
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        System.out.println("Available game modes: \n 1.Player vs Player \n 2.Player vs AI \n Type number to choose the game mode");

        try{
            gameMode = Integer.parseInt(reader.readLine());     //! переделать под switch!
            if(gameMode != 1 && gameMode != 2) {                // Если gameMode не равен ожидаемому значению, то
                gameMode = 2;                                   // выбрасывается исключение. Значение gameMode
                throw new IOException();                        // устанавливается по умолчанию. Процедура ввода
            }                                                   // повторяется пока игра не получит ожидаемое значение, после
            reader.close();                                     // чего поток закрывается
        } catch (IOException | NumberFormatException exception) {
            System.out.println("Incorrect Input");
            chooseGameMode();
        }

    }

    private void actionPhase() {                // Вариант реализации: словарь ключ - координата ячейки в игровом поле, значение - адрес ячейки массива
    }

    private void endGameConditionCheck() {      // Вариант реализации: проверка по каждому варианту завершения игры (9 исходов: 8 побед, 1 ничья)

    }
}