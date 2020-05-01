package Othello;
//Elyahu Jacobi
import java.util.Arrays;

enum CellState {NONE, BLACK, WHITE}

public class OthelloModel implements OthelloModelInterface {
    private static CellState[][] grid;
    private static final int GRID_SIZE = 8;

    OthelloModel() {
        grid = new CellState[GRID_SIZE][GRID_SIZE];
        for (CellState[] cellStates : grid) {
            Arrays.fill(cellStates, CellState.NONE);
        }
        grid[(GRID_SIZE / 2) - 1][(GRID_SIZE / 2) - 1] = CellState.WHITE;
        grid[GRID_SIZE / 2][GRID_SIZE / 2] = CellState.WHITE;
        grid[(GRID_SIZE / 2) - 1][GRID_SIZE / 2] = CellState.BLACK;
        grid[GRID_SIZE / 2][(GRID_SIZE / 2) - 1] = CellState.BLACK;
    }

    @Override
    public boolean checkGameOver() {
        if (gameOver()) {
            endGame();
            return true;
        } else {
            return false;
        }
    }

    private void flipInAllDir(int row, int col, CellState state) {
        grid[row][col] = state;
        for (int i = 0; i < GRID_SIZE; i++) {
            flipPieces(row, col, state, i);
        }
    }

    private void flipPieces(int row, int col, CellState state, int dir) {
        int[] dirI = {-1,1,0,0,1,-1,1,-1};// these are all the directions to check
        int[] dirJ = {0,0,1,-1,1,-1,-1,1};
        if(isFlippable(state, row, col, dir)) {
            row+=dirI[dir];
            col+=dirJ[dir];

            while(grid[row][col] != state) {
                grid[row][col] = state;
                row += dirI[dir];
                col += dirJ[dir];
            }
        }
    }

    private boolean gameOver() {
        int countTotal = 0;
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (grid[i][j] == CellState.BLACK || grid[i][j] == CellState.WHITE) {
                    countTotal++;
                }
            }
        }
        return (countTotal == GRID_SIZE * GRID_SIZE);
    }

    public CellState endGame() {
        int countWhite = 0;
        int countBlack = 0;
        for (CellState[] cellStates : grid) {
            for (CellState cellState : cellStates) {
                if (cellState == CellState.BLACK) {
                    countBlack++;
                } else if (cellState == CellState.WHITE) {
                    countWhite++;
                }
            }
        }
        if (countBlack > countWhite) {
            System.out.println("Game over! The winner is black!");
            return CellState.BLACK;
        } else if (countWhite > countBlack) {
            System.out.println("Game over! The winner is white!");
            return CellState.WHITE;
        } else {
            System.out.println("Game over. It is a tie game.");
        }
        return null;
    }

    private boolean isFlippable(CellState state, int row, int col, int dir) {
        int[] dirI = {-1,1,0,0,1,-1,1,-1};
        int[] dirJ = {0,0,1,-1,1,-1,-1,1};
        CellState opponent = CellState.BLACK;
        if (state == CellState.BLACK){
            opponent = CellState.WHITE;
        }
        boolean isFlippable = false;
        for(int i = 0; i < 7; i++) {
            row += dirI[dir];
            col += dirJ[dir];
            if (row < 0 || row > GRID_SIZE - 1) {
                continue;
            }
            if (col < 0 || col > GRID_SIZE - 1) {
                continue;
            }
            if (grid[row][col] == opponent) {
                isFlippable = true;
            } else if (grid[row][col] == state) {
                return isFlippable;
            } else{
                return false;
            }
        }
        return false;
    }

    @Override
    public boolean makeMove(int row, int col, CellState state) {
        grid[row][col] = state;
        flipInAllDir(row, col, state);
        return true;
    }

    @Override
    public boolean isMoveLegal(int row, int col, CellState state) {
        return isLocationAvailable(row, col) && isMoveFlippable(row, col, state);
    }

    @Override
    public boolean isMoveFlippable(int row, int col, CellState state) {
            for(int dir = 0; dir < 8; dir++) {
                if (isFlippable(state, row, col, dir)) {
                    return true;
                }
            }
        return false;
    }

    @Override
    public boolean isLocationAvailable(int row, int col) {
        if (row < 0 || row > GRID_SIZE - 1)
            return false;
        return grid[row][col] == CellState.NONE;
    }

    @Override
    public CellState getCellState(int row, int col) {
        return grid[row][col];
    }
}