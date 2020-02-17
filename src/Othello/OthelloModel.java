package Othello;
//Elyahu Jacobi

enum CellState {NONE, BLACK, WHITE}

public class OthelloModel implements OthelloModelInterface {
    private static CellState[][] grid;
    private final int GRID_SIZE = 8;

    public OthelloModel() {
        grid = new CellState[GRID_SIZE][GRID_SIZE];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                grid[i][j] = CellState.NONE;
            }
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
        }else{
            return false;
        }
    }

    private void flipAllDir(int row, int col, CellState state) {
        flipPieces(row, col, state, 0, 1);//check up
        flipPieces(row, col, state, 0, -1);//check down
        flipPieces(row, col, state, 1, 0); //check left
        flipPieces(row, col, state, -1, 0);//check right
        flipPieces(row, col, state, 1, 1);//check corners
        flipPieces(row, col, state, 1, -1);
        flipPieces(row, col, state, -1, 1);
        flipPieces(row, col, state, -1, -1);
    }

    private void flipPieces(int row, int col, CellState state, int rowDir, int colDir) {
        int checkedRow = row + rowDir;
        int checkedCol = col + colDir;
        if (checkedRow == 8 || checkedRow < 0 || checkedCol == 8 || checkedCol < 0) {
            return;
        }
        while (grid[checkedRow][checkedCol] == CellState.BLACK || grid[checkedRow][checkedCol] == CellState.WHITE) {
            if (grid[checkedRow][checkedCol] == state) {
                while (!(row == checkedRow && col == checkedCol)) {
                    grid[checkedRow][checkedCol] = state;
                    checkedRow = checkedRow - rowDir;
                    checkedCol = checkedCol - colDir;
                }
                break;
            } else {
                checkedRow = checkedRow + rowDir;
                checkedCol = checkedCol + colDir;
            }
            if (checkedRow < 0 || checkedCol == 8 || checkedRow == 8 || checkedCol < 0) {
                break;
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

    private void endGame() {
        int countWhite = 0;
        int countBlack = 0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == CellState.BLACK) {
                    countBlack++;
                } else if (grid[i][j] == CellState.WHITE) {
                    countWhite++;
                }
            }
        }
        if (countBlack > countWhite) {
            System.out.println("Game over! The winner is black!");
        } else if (countWhite > countBlack) {
            System.out.println("Game over! The winner is white!");
        } else {
            System.out.println("Game over. It is a tie game.");
        }
    }

    @Override
    public boolean makeMove(int row, int col, CellState state) {
        grid[row][col] = state;
        flipAllDir(row, col, state);
        return true;
    }

    @Override
    public boolean isMoveLegal(int row, int col, CellState state) {
        return isLocationAvailable(row, col) && isMoveFlippable(row, col, state);
    }

    @Override
    public boolean isMoveFlippable(int row, int col, CellState state) {
        boolean result = false;
        CellState oppCol = CellState.BLACK;
        if (state == CellState.BLACK) {
            oppCol = CellState.WHITE;
        }
        if (row + 1 < 8 && col + 1 < 8 && grid[row + 1][col + 1] == oppCol) {
            result = true;
        } else if (row + 1 < 8 && grid[row + 1][col] == oppCol) {
            result = true;
        } else if (col + 1 < 8 && grid[row][col + 1] == oppCol) {
            result = true;
        } else if (col - 1 > -1 && grid[row][col - 1] == oppCol) {
            result = true;
        } else if (row - 1 > -1 && col - 1 > -1 && grid[row - 1][col - 1] == oppCol) {
            result = true;
        } else if (row - 1 > -1 && grid[row - 1][col] == oppCol) {
            result = true;
        } else if (row - 1 > -1 && col + 1 < 8 && grid[row - 1][col + 1] == oppCol) {
            result = true;
        } else if (row + 1 < 8 && col - 1 > -1 && grid[row + 1][col - 1] == oppCol) {
            result = true;
        }
        return result;
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