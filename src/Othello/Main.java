package Othello;

import java.util.Scanner;

public class Main {
/*Elyahu Jacobi*/
    public static void main(String[] args) {
        OthelloModelInterface othelloModel = new OthelloModel();
        int input;
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter 1 for Othello in GUI");
        System.out.println("Enter 2 for Two player Othello in the console");
        System.out.println("Enter 3 for One player Othello in the console");
        input = scanner.nextInt();
        if (input == 1)
            new OthelloWindow(othelloModel);
        if (input == 2)
        new OthelloConsole(othelloModel);
        if (input == 3)
        new OthelloModelOnePlayer(othelloModel);
    }
}