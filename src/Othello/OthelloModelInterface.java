package Othello;
//Elyahu Jacobi
public interface OthelloModelInterface {
    boolean checkGameOver();
    boolean makeMove(int row, int col, CellState state);
    boolean isMoveLegal(int row, int col, CellState state);
    boolean isMoveFlippable(int row, int col, CellState state);
    boolean isLocationAvailable(int row, int col);
    CellState getCellState(int row,int col);
}