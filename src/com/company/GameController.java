package com.company;

import java.io.IOException;
import java.io.Serializable;

public class GameController implements Serializable {

    private static GameController instance;

    private static char[][] gameField = new char[3][3];        //игровое поле 3х3
    private static int gameMode = 2;
    private int emptyField = 9;
    private boolean endGame = false;
    private Player player1;
    private Player player2;
    static GameLog logger;

    private GameController() {}

    public static GameController getInstance() {            //потокобезопасный синглтон
        synchronized (GameController.class){
            if(instance == null){
                instance = new GameController();
            }
            return instance;
        }
    }

    public void setPlayer1(Player player1) {
        this.player1 = player1;
    }

    public void setPlayer2(Player player2) {
        this.player2 = player2;
    }

    public void setGameMode(int x) {
        gameMode = x;
    }

    public void startGame(){                          //Публичный метод, запускающий игровой процесс
        initGameField();
        logger = new GameLog();
        actionPhase();
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

    private void actionPhase() {               // Активная фаза игры. Ввод игроками координат

        boolean flag = true;                   // flag - true: ход 1 игрока, flag - false: ход 2 игрока

        while (!endGame){
            int x;
            int y;

            drawField();
            endGameConditionsCheck();

            if(endGame) {
                System.out.println("Game Over!");
                if(emptyField == 0) {
                    System.out.println("Draw! No one wins!");
                    logger.setGameRes("Draw");
                }
                else if(!flag) {
                    System.out.println(player1.getPlayerName() + " wins!");
                    player1.gamesWonInc();
                    logger.setGameRes(player1.getPlayerName() + " won");
                }
                else if(gameMode == 1) {
                    System.out.println(player2.getPlayerName() +" wins!");
                    player2.gameLogs.add(logger);
                    logger.setGameRes(player2.getPlayerName() + " won");
                    player2.gamesWonInc();
                }
                else if(gameMode == 2) {
                    System.out.println("AI wins!");
                    logger.setGameRes("AI won");
                }

                player1.gameLogs.add(logger);
                player1.gamesTotalInc();
                if(player2 != null) { player2.gamesTotalInc(); }

                System.out.println("Game log saved with ID: " + logger.getGameID());
                endGame = false;
                WelcomeMenu.getInstance().start();
            }

            if(flag) System.out.println(player1.getPlayerName() + " turn! Enter cell coordinate(ex. A 0):");
            else if(gameMode == 1){ System.out.println(player2.getPlayerName() + " turn! Enter cell coordinate(ex. A 0):"); }
            else { System.out.println("AI turn!"); }

            if(gameMode == 2 && !flag) {
                AI.makeMove();
                actionPhase();
            }

            try {
                String input = Main.reader.readLine().toUpperCase();

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
                    if(flag) {                                              // соответствующего символа, иначе выброс
                        gameField[x][y] = 'X';                              // исключения
                        logger.gameLog.add(player1.getPlayerName() + " (X)" + " : " + input);
                    }
                    else {
                        gameField[x][y] = '0';
                        logger.gameLog.add(player2.getPlayerName() + " (0)" + " : " + input);
                    }
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
                        GameController.logger.gameLog.add("AI (0)" + " : " + coordinateReplacer(xColumnAxis) +" " + yColumnAxis);
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
                        GameController.logger.gameLog.add("AI (0)" + " : " + coordinateReplacer(xRowAxis) +" " + yRowAxis);
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
                    GameController.logger.gameLog.add("AI (0)" + " : " + coordinateReplacer(axisDiagTopLeft) +" " + axisDiagTopLeft);
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
                    GameController.logger.gameLog.add("AI (0)" + " : " + coordinateReplacer(xAxisDiagTopRight) +" " + yAxisDiagTopRight);
                    gameField[xAxisDiagTopRight][yAxisDiagTopRight] = '0';
                    return false;
                }

                diagOffset--;
            }

            return true;
        }

        private static void offence() {                 // Набросок атаки ИИ. Приоритет на захват центра. Дальше рандом,
                                                        // если не вызывается метод defence. Если будет время - сделать нормальный ИИ.
            int xAxis = (int) (Math.random() * 3);
            int yAxis = (int) (Math.random() * 3);

            System.out.println(xAxis);
            System.out.println(yAxis);

            if(gameField[1][1] == ' ') {
                GameController.logger.gameLog.add("AI (0)" + " : " + coordinateReplacer(1) +" " + 1);
                gameField[1][1] = '0';
                return;
            }

            if(gameField[xAxis][yAxis] != ' ') {
                offence();
            } else {
                GameController.logger.gameLog.add("AI (0)" + " : " + coordinateReplacer(xAxis) +" " + yAxis);
                gameField[xAxis][yAxis] = '0';
            }
        }

        private static String coordinateReplacer (int x) {  // Замена координат горизонтали на буквенные для записи игры
            switch (x){
                case 0: { return "A";}
                case 1: { return "B";}
                case 2: { return "C";}
            }
            return null;
        }
    }
}