package Othello;

import java.util.Scanner;

public class OthelloConsole {
    Scanner scanner = new Scanner(System.in);
    private final int GRID_SIZE = 8;
    OthelloConsole(OthelloModelInterface othelloModelInterface){
        gameStart(othelloModelInterface);
    }

    private void gameStart(OthelloModelInterface othelloModel) {
        while (!othelloModel.checkGameOver()){
            print(othelloModel);
            player1Turn(othelloModel);
            print(othelloModel);
            player2Turn(othelloModel);
        }
        othelloModel.checkGameOver();
    }

    private void player1Turn(OthelloModelInterface othelloModel) {
        int row, col;
        System.out.println("Black's turn");
        System.out.println("Please input which row:");
        row = scanner.nextInt() - 1;
        System.out.println("Please input which column:");
        col = scanner.nextInt() - 1;
        if(!othelloModel.isMoveLegal(row,col,CellState.BLACK)){
            System.out.println("Sorry, not a valid move for black.");
            player1Turn(othelloModel);
        }else {
            othelloModel.makeMove(row,col,CellState.BLACK);
        }
    }

    private void player2Turn(OthelloModelInterface othelloModel) {
        int row, col;
        System.out.println("White's turn");
        System.out.println("Please input which row:");
        row = scanner.nextInt() - 1;
        System.out.println("Please input which column:");
        col = scanner.nextInt() - 1;
        if(!othelloModel.isMoveLegal(row,col,CellState.WHITE)){
            System.out.println("Sorry, not a valid move for white.");
            player2Turn(othelloModel);
        }else {
            othelloModel.makeMove(row,col,CellState.WHITE);
        }
    }
    private void print(OthelloModelInterface othellomodel){
        System.out.println("0-1--2--3--4--5--6--7--8-");
        for (int i = 0; i <GRID_SIZE; i++) {
            System.out.print(""+(i+1));
            for (int j = 0; j <GRID_SIZE; j++) {
                if (othellomodel.getCellState(i,j) == CellState.NONE){
                    System.out.print("- -");
                }
                if (othellomodel.getCellState(i,j) == CellState.WHITE){
                    System.out.print("-W-");
                }else if (othellomodel.getCellState(i,j) == CellState.BLACK){
                    System.out.print("-B-");
                }
            }System.out.println();
        }
    }
}
