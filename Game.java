package michael.khodkov;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class Game {

    private static Random random = new Random();
    private static char[][] field = new char[3][3];
    private static  JButton[] buttonArray = new JButton[9];
    private static final char SYMBOL_EMPTY = ' ';
    private static final char SYMBOL_X = 'X';
    private static final char SYMBOL_O = 'O';
    private static final int SIZE_FIELD = 3;
    private static boolean flagNotSecondTurn = false;
    private static int ALL_GAME = 0;
    private static int WIN_HERO = 0;
    private static int WIN_AI = 0;
    private static boolean flagHardLevel = false;
    private static boolean isAiTurn = false;


    public static void main(String[] args) {
        ALL_GAME++;
        initField();
        newGame(drawField());
    }

    private static void newGame(JFrame window){
        do {


            String[] level = new String[2];
            level[0] = "Легкий";
            level[1] = "Профи";
            ImageIcon icon = null;
            Object rezult = JOptionPane.showInputDialog(window, "Выберете уровень сложности:",
                    "Выбор уровня сложности", JOptionPane.QUESTION_MESSAGE, icon, level, level[0]);
            if (rezult == null){
                continue;
            }
            if (rezult.equals(level[1])) {
                flagHardLevel = true;
            }
            if (!coinFlip()) { //кто ходит первым
                JOptionPane.showMessageDialog(window, "Первым ходит компьютер");
                isAiTurn = true;
                computerTurn(window);
            } else {
                JOptionPane.showMessageDialog(window, "Первым ходит игрок");
            }
            break;
        }while (true);
    }

    private static JFrame drawField(){
        JFrame window = new JFrame("Game");
        window.setBounds(300, 300, 300, 300);
        window.getContentPane().setLayout(new GridLayout(3, 3, 5, 5));
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        for (int i = 0; i < buttonArray.length; i++) {
            buttonArray[i] = new JButton();
            window.add(buttonArray[i]);
            int index = i;
            buttonArray[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    buttonArray[index].setEnabled(false);
                    if (index < 3){
                        setXO(index, 0, SYMBOL_X);
                    }else if (index < 6){
                        setXO(index - 3, 1, SYMBOL_X);
                    }else {
                        setXO(index - 6, 2, SYMBOL_X);
                    }
                    if (isWin(SYMBOL_X)){
                        windowAnswer(window, "Победил игрок");
                        WIN_HERO++;
                    }
                    if (isEnd()){
                        windowAnswer(window, "Ничья");
                    }
                    computerTurn(window);
                    if (isWin(SYMBOL_O)){
                        windowAnswer(window, "Победил компьютер");
                        WIN_AI++;
                    }
                    if (isEnd()){
                        windowAnswer(window, "Ничья");
                    }
                }
            });
        }

        window.setResizable(false);
        window.setVisible(true);
        return window;
    }

    private static void initField() {
        for (int y = 0; y < SIZE_FIELD; y++) {
            for (int x = 0; x < SIZE_FIELD; x++) {
                field[y][x] = SYMBOL_EMPTY;
            }
        }
    }

    private static void windowAnswer(JFrame window, String mesage){
        int answer = JOptionPane.showConfirmDialog(window, "Играть заново?", mesage,
                JOptionPane.YES_NO_OPTION);
        if (answer == JOptionPane.YES_OPTION) {
            window.dispose();
            String[] st = new String[0];
            main(st);
        } else {
            JOptionPane.showMessageDialog(window, "Всего игр: " + ALL_GAME + "\nПобед: " + WIN_HERO +
                    "\nНичьих: " + (ALL_GAME - WIN_HERO - WIN_AI) + "\nПоражений: " + WIN_AI);
            System.exit(0);
        }
    }

    private static boolean coinFlip(){
        int coin = 0;
        for (int i = 0; i < 10; i++) {
            coin += random.nextInt(100);
        }
        if (coin / 10 > 40){
            return true;
        }
        else {
            return false;
        }
    }

    private static void computerTurn(JFrame window) {
        // стараемся занять центр
        if (isAiTurn) {
            isAiTurn = false;
            int y;
            int x;
            if (flagHardLevel && (setXO(1, 1, SYMBOL_O))) {
                return;
            }
            while (true) {
                int danger = SYMBOL_X * 2 + SYMBOL_EMPTY; // опасность, 2 подряд Х игрока
                int lucky = SYMBOL_O * 2 + SYMBOL_EMPTY; // шанс на победу, 2 подряд О
                int arc = SYMBOL_EMPTY * 2; // углы
                int line = SYMBOL_O + SYMBOL_EMPTY * 2; // один наш символ и 2 свободных поля
                int count = 5; // триггер хода
                int search = lucky; // сначала будем искать победный ход
                while (true) { // бесконечный цикл, выход при установке символа О на поле
                    int sumHorizontal = 0;
                    int sumVertical = 0;
                    int sumDiagonalDirect = 0;
                    int sumDiagonalRevers = 0;
                    //если есть что искать, запускаем 2 цикла чтобы проанализировать поле
                    if (search > 0) {
                        for (int i = 0; i < SIZE_FIELD; i++) {
                            for (int j = 0; j < SIZE_FIELD; j++) {
                                sumHorizontal += field[j][i]; // считаем горизонталь
                                sumVertical += field[i][j]; // считаем вертикаль
                            }
                            // проверяем, есть ли в гориpонтале то что ищем, но если ищем угл, сюда не заходим
                            if (sumHorizontal == search && search != arc) {
                                for (int k = 0; k < SIZE_FIELD; k++) {
                                    if (setXO(k, i, SYMBOL_O)) {
                                        return;
                                    }
                                }
                            }
                            // проверяем, есть ли в вертикале то что ищем, но если ищем угл, сюда не заходим
                            if (sumVertical == search && search != arc) {
                                for (int k = 0; k < SIZE_FIELD; k++) {
                                    if (setXO(i, k, SYMBOL_O)) {
                                        return;
                                    }
                                }
                            } else {
                                //считаем диагонали, обнуляем горизонталь и вертикаль для следующего индекса
                                sumDiagonalDirect += field[i][i];
                                sumDiagonalRevers += field[i][2 - i];
                                sumHorizontal = 0;
                                sumVertical = 0;
                            }
                        }
                        // проверяем, есть ли в даагонале (\) то что ищем
                        if (sumDiagonalDirect == search) {
                            for (int i = 0; i < SIZE_FIELD; i++) {
                                if (setXO(i, i, SYMBOL_O)) {
                                    return;
                                }
                            }
                            // проверяем, есть ли в обратной диагонали (/) то что ищем
                        } else if (sumDiagonalRevers == search) {
                            for (int i = 0; i < SIZE_FIELD; i++) {
                                if (setXO(i, 2 - i, SYMBOL_O)) {
                                    return;
                                }
                            }
                        }
                    }
                    // переключаем триггер
                    count--;
                    if (count == 4) { // будем искать 2 подряд символа Х
                        search = danger;
                    } else if (count == 3 && flagNotSecondTurn && flagHardLevel) { // будем искать треугольник
                        if (field[1][1] == SYMBOL_O) { // проверяем, наш ли центр
                            search = line;
                        } else
                            search = -1;
                    } else if (count == 2 && flagHardLevel) { // будем ставить в угл
                        arc = arc + field[1][1];
                        search = arc;
                        flagNotSecondTurn = true;
                    } else if (count == 1) { // пробуем найти линию где есть шанс на победу
                        search = line;
                    } else if (count < 1) { // будем ставить в любое место
                        //System.out.println(count);
                        y = random.nextInt(3);
                        x = random.nextInt(3);
                        if (setXO(y, x, SYMBOL_O)) {
                            return;
                        }
                    }
                }
            }
        }
    }

    private static boolean setXO(int y, int x, char xo){
        if (x > SIZE_FIELD - 1 || x < 0 || y > SIZE_FIELD - 1 || y < 0){
            return false;
        }else if (field[y][x] == SYMBOL_EMPTY){
            field[y][x] = xo;
            buttonArray[x * 3 + y].setText(String.valueOf(xo));
            buttonArray[x * 3 + y].setEnabled(false);
            if (xo == SYMBOL_X){
                isAiTurn = true;
            }
            return true;

        }else {
            return false;
        }
    }

    // вернет true если нет хода
    public static boolean isEnd(){
        for (int i = 0; i < SIZE_FIELD; i++) {
            for (int j = 0; j < SIZE_FIELD; j++) {
                if (field[i][j] == SYMBOL_EMPTY){
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean isWin(char symbol) {
        Color heroColor = Color.ORANGE;
        Color aiColor = Color.PINK;
        Color setColor = null;
        if (symbol == SYMBOL_X){
            setColor = heroColor;
        }
        else{
            setColor = aiColor;
        }
        int sumHorizontal = 0;
        int sumVertical = 0;
        int sumDiagonalDirect = field[0][0] + field[1][1] + field[2][2];
        int sumDiagonalRevers = field[0][2] + field[1][1] + field[2][0];
        int win = symbol * 3; // победная сумма
        if (sumDiagonalDirect == win){
            buttonArray[0].setBackground(setColor);
            buttonArray[4].setBackground(setColor);
            buttonArray[8].setBackground(setColor);
            return true;
        }
        else if (sumDiagonalRevers == win){
            buttonArray[2].setBackground(setColor);
            buttonArray[4].setBackground(setColor);
            buttonArray[6].setBackground(setColor);
            return true;
        }
        else {
            for (int i = 0; i < SIZE_FIELD; i++) {
                for (int j = 0; j < SIZE_FIELD; j++) {
                    sumHorizontal += field[j][i];
                    sumVertical += field[i][j];
                }
                if (sumHorizontal == win){
                    buttonArray[i * 3].setBackground(setColor);
                    buttonArray[i * 3 + 1].setBackground(setColor);
                    buttonArray[i * 3 + 2].setBackground(setColor);
                    return true;
                }
                else if (sumVertical == win){
                    buttonArray[i].setBackground(setColor);
                    buttonArray[i + 3].setBackground(setColor);
                    buttonArray[i + 6].setBackground(setColor);
                    return true;
                }
                else {
                    sumHorizontal = 0;
                    sumVertical = 0;
                }
            }
        }
        return false;
    }
}
