package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class GameController {

    private static GameController instance;

    BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

    private static char[][] gameField = new char[3][3];        //игровое поле 3х3
    private static int turnCounter = 1;
    private static int gameMode = 2;
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
            gameMode = Integer.parseInt(reader.readLine());
            if(gameMode != 1 && gameMode != 2) {                // Если gameMode не равен ожидаемому значению, то
                gameMode = 2;                                   // выбрасывается исключение. Значение gameMode
                throw new IOException();                        // устанавливается по умолчанию. Процедура ввода
            }                                                   // повторяется пока игра не получит ожидаемое значение
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
                if(emptyField == 0) { System.out.println("Draw! No one wins!");}
                else if(!flag) {System.out.println(player1name + " wins!");}
                else if(gameMode == 1) {System.out.println(player2name +" wins!");}
                else if(gameMode == 2) {System.out.println("AI wins!");}
                System.exit(0);
            }

            if(flag) System.out.println(player1name + " turn! Enter cell coordinate(ex. A 0):");
            else if(gameMode == 1){ System.out.println(player2name + " turn! Enter cell coordinate(ex. A 0):"); }
            else { System.out.println("AI turn!"); }

            if(gameMode == 2 && !flag) {
                AI.makeMove();
                actionPhase();
            }

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
                    else {gameField[x][y] = '0';}                           // исключения
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

    private void endGameConditionsCheck() {

        emptyField = 9;

        for (int a = 0; a < 3; a++){            // проверка по столбцам
            if(gameField[a][0] != ' ' && gameField[a][0] == gameField[a][1] && gameField[a][0] == gameField[a][2]){
                endGame = true;
                return;
            }
        }

        for (int a = 0; a < 3; a++){            // проверка по строкам
            if (gameField[0][a] != ' ' && gameField[0][a] == gameField[1][a] && gameField[0][a] == gameField[2][a]) {
                endGame = true;
                return;
            }
        }

        // проверка по диагонали свреху слева до вниз справа
        if(gameField[0][0] != ' ' && gameField[0][0] == gameField[1][1] && gameField[0][0] == gameField[2][2]){
            endGame = true;
            return;
        }

        // проверка по диагонали снизу слева до сверху справа
        if(gameField[0][2] != ' ' && gameField[0][2] == gameField[1][1] && gameField[0][2] == gameField[2][0]){
            endGame = true;
            return;
        }

        for(int a = 0; a < 3; a++) {                    // проверка на ничью
            for(int b = 0; b < 3; b++) {
                if(gameField[a][b] != ' '){
                    emptyField--;
                    if(emptyField == 0) {
                        endGame = true;
                        return;
                    }
                }
            }
        }
    }

    private static class AI {

        static void makeMove() {           // Алгоритм хода ИИ. Если метод defence соверщил ход - метод offence игнорируется.
            if(defence()) { offence(); }
        }

        private static boolean defence() {
                                                    // Пробег по всем полям
            for(int a = 0; a < 3; a++){
                                                    // Переменные для рядов
                int xColumnCells = 0;               // Счётчик занятых противником клеток в ряду
                int emptyColumnCells = 3;           // Счетчик пустых клеток в ряду
                int xColumnAxis = 0;                // координата заменяемой ячейки по горизонтали
                int yColumnAxis = 0;                // координата заменяемой ячейки по вертикали
                                                    // Переменные для строк
                int xRowCells = 0;                  // см. Переменные для рядов
                int emptyRowCells = 3;
                int xRowAxis = 0;
                int yRowAxis = 0;

                for(int b = 0; b < 3; b++) {
                    if (gameField[a][b] == 'X') {       // Проверка по столбцам
                        xColumnCells++;
                        emptyColumnCells--;

                    } else if(gameField[a][b] == ' '){  // Захват координат последнего пустого поля в ряду
                        xColumnAxis = a;
                        yColumnAxis = b;
                    }
                                                        // Меняет значение ячейки по полученным координатам, если в ряду присутствует потенциальое поражение ИИ.
                    if(xColumnCells == 2 && emptyColumnCells == 1 && gameField[xColumnAxis][yColumnAxis] == ' ') {
                        gameField[xColumnAxis][yColumnAxis] = '0';
                        return false;
                    }

                    if (gameField[b][a] == 'X') {       // Проверка по строкам
                        xRowCells++;
                        emptyRowCells--;

                    } else if(gameField[b][a] == ' '){
                        xRowAxis = b;
                        yRowAxis = a;
                    }

                    if(xRowCells == 2 && emptyRowCells == 1 && gameField[xRowAxis][yRowAxis] == ' ') {
                        gameField[xRowAxis][yRowAxis] = '0';
                        return false;
                    }
                }
            }

            int xDiagTopLeft = 0;                       // Левая верхняя диагональ
            int axisDiagTopLeft = 0;

            for(int a = 0; a < 3; a++){
                if(gameField[a][a] == 'X') {
                    xDiagTopLeft++;
                } else if(gameField[a][a] == ' '){
                    axisDiagTopLeft = a;
                }

                if(xDiagTopLeft == 2 && gameField[axisDiagTopLeft][axisDiagTopLeft] == ' '){
                    gameField[axisDiagTopLeft][axisDiagTopLeft] = '0';
                    return false;
                }
            }

            int xDiagTopRight = 0;                          // Правая верхняя диагональ
            int xAxisDiagTopRight = 0;
            int yAxisDiagTopRight = 0;
            int diagOffset = 2;

            for(int a = 0; a < 3; a++) {
                if(gameField[a][diagOffset] == 'X') {
                    xDiagTopRight++;
                } else if(gameField[a][diagOffset] == ' '){
                    xAxisDiagTopRight = a;
                    yAxisDiagTopRight = diagOffset;
                }
                if(xDiagTopRight == 2 && gameField[xAxisDiagTopRight][yAxisDiagTopRight] == ' '){
                    gameField[xAxisDiagTopRight][yAxisDiagTopRight] = '0';
                    return false;
                }

                diagOffset--;
            }

            return true;
        }

        private static void offence() {                 // Набросок атаки ИИ. Приоритет на захват центра. Дальше рандом,
            int a = 0;                                  // ессли не вызывается метод defence. Если будет время - сделать нормальный ИИ.
            int b = 2;

            int xAxis = a + (int) (Math.random() * b) ;
            int yAxis = a + (int) (Math.random() * b) ;;

            if(gameField[1][1] == ' ') {
                gameField[1][1] = '0';
                return;
            }

            if(gameField[xAxis][yAxis] != ' '){
                offence();
            } else {
                gameField[xAxis][yAxis] = '0';
                return;
            }
        }
    }
}