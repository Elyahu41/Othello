package Othello;
//Elyahu Jacobi
import java.util.ArrayList;
import java.util.Scanner;

public class OthelloModelOnePlayer implements OthelloModelInterface{
    OthelloModel othelloModel = new OthelloModel();
    private final int GRID_SIZE = 8;
    Scanner scanner = new Scanner(System.in);
    OthelloModelOnePlayer(OthelloModelInterface othelloModelInterface){
        gameStart(othelloModelInterface);
    }

    private void gameStart(OthelloModelInterface othelloModel) {
        while (!othelloModel.checkGameOver()){
            print(othelloModel);
            player1Turn(othelloModel);
            print(othelloModel);
            computerTurn(othelloModel);
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

    private void computerTurn(OthelloModelInterface othelloModel) {
        int row, col;
        String s = getGreedyMove(othelloModel);
        row = Integer.parseInt(String.valueOf(s.charAt(0)));
        col = Integer.parseInt(String.valueOf(s.charAt(1)));
        System.out.println("White's turn");
        if(!othelloModel.isMoveLegal(row,col,CellState.WHITE)){
            System.out.println("No turns; skipping...");
        }else {
            othelloModel.makeMove(row,col,CellState.WHITE);
        }
    }

    private String getGreedyMove(OthelloModelInterface othelloModel) {
        int numberOfFlips = 0;
        CellState oppCol = CellState.BLACK;
        ArrayList<String> allMoves = new ArrayList<>(60);
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (i + 1 < 8 && j + 1 < 8 && othelloModel.getCellState(i + 1,j + 1) == oppCol) {
                    numberOfFlips++;
                }
                if (i + 1 < 8 && othelloModel.getCellState(i + 1,j) == oppCol) {
                    numberOfFlips++;
                }
                if (j + 1 < 8 && othelloModel.getCellState(i,j + 1) == oppCol) {
                    numberOfFlips++;
                }
                if (j - 1 > -1 && othelloModel.getCellState(i,j - 1) == oppCol) {
                    numberOfFlips++;
                }
                if (i - 1 > -1 && j - 1 > -1 && othelloModel.getCellState(i - 1,j - 1) == oppCol) {
                    numberOfFlips++;
                }
                if (i - 1 > -1 && othelloModel.getCellState(i - 1,j) == oppCol) {
                    numberOfFlips++;
                }
                if (i - 1 > -1 && j + 1 < 8 && othelloModel.getCellState(i - 1,j + 1) == oppCol) {
                    numberOfFlips++;
                }
                if (i + 1 < 8 && j - 1 > -1 && othelloModel.getCellState(i + 1,j - 1) == oppCol) {
                    numberOfFlips++;
                }
                if (numberOfFlips != 0 && isMoveLegal(i,j,CellState.WHITE)){
                    allMoves.add(""+i+j+numberOfFlips);
                }
                numberOfFlips=0;
            }
        }
        String bestMove = allMoves.get(0);
        for (int i = 1; i < allMoves.size(); i++) {
            int moveInList = Integer.parseInt(String.valueOf(allMoves.get(i).charAt(2)));
            if (moveInList > Integer.parseInt(String.valueOf(bestMove.charAt(2)))) {
                bestMove = allMoves.get(i);
            }
        }
        return bestMove;
    }


    @Override
    public boolean checkGameOver() {
        return othelloModel.checkGameOver();
    }

    @Override
    public CellState endGame() {
        return othelloModel.endGame();
    }

    @Override
    public boolean makeMove(int row, int col, CellState state) {
        return othelloModel.makeMove(row, col, state);
    }

    @Override
    public boolean isMoveLegal(int row, int col, CellState state) {
        return othelloModel.isMoveLegal(row, col, state);
    }

    @Override
    public boolean isMoveFlippable(int row, int col, CellState state) {
        return othelloModel.isMoveFlippable(row, col, state);
    }

    @Override
    public boolean isLocationAvailable(int row, int col) {
        return othelloModel.isLocationAvailable(row, col);
    }

    @Override
    public CellState getCellState(int row, int col) {
        return othelloModel.getCellState(row, col);
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
