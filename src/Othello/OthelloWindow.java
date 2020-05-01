package Othello;
//Elyahu Jacobi
import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;

class OthelloWindow extends JFrame {
    private int GRID_SIZE = 8;
    private AtomicBoolean isBlackTurn = new AtomicBoolean(true);
    private JButton[][] buttons = new JButton[GRID_SIZE][GRID_SIZE];

    OthelloWindow(OthelloModelInterface othelloModelInterface) {
        setTitle("Othello");
        this.setSize(700, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(GRID_SIZE, GRID_SIZE));
        for (int row = 0; row < GRID_SIZE; row++) { //Setup Grid
            for (int col = 0; col < GRID_SIZE; col++) {
                JButton b = new JButton();
                buttons[row][col] = b;
                add(b);
                b.setName("" + row + col);
                b.addActionListener(actionEvent -> {
                    int clickedRow = Integer.parseInt(String.valueOf(b.getName().charAt(0)));
                    int clickedCol = Integer.parseInt(String.valueOf(b.getName().charAt(1)));

                    if (isBlackTurn.get() && othelloModelInterface.isMoveLegal(clickedRow, clickedCol, CellState.BLACK)) {
                        othelloModelInterface.makeMove(clickedRow, clickedCol, CellState.BLACK);
                        update(othelloModelInterface);
                        togglePlayer();
                    } else if (!isBlackTurn.get() && othelloModelInterface.isMoveLegal(clickedRow, clickedCol, CellState.WHITE)) {
                        othelloModelInterface.makeMove(clickedRow, clickedCol, CellState.WHITE);
                        update(othelloModelInterface);
                        togglePlayer();
                    }
                });
            }
        }
        update(othelloModelInterface);//for middle pieces
        this.setVisible(true);
    }

        private void togglePlayer() {
        if (isBlackTurn.get()) {
                isBlackTurn.set(false);
            } else {
                isBlackTurn.set(true);
            }
        }

    private void update(OthelloModelInterface othelloModelInterface) {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                JButton b = buttons[i][j];
                CellState cellState = othelloModelInterface.getCellState(i, j);

                if (cellState == CellState.WHITE) {
                    b.setBackground(Color.WHITE);
                } else if (cellState == CellState.BLACK) {
                    b.setBackground(Color.BLACK);
                } else{
                    b.setBackground(new Color(39,134,50));
                }
            }
            revalidate();
            othelloModelInterface.checkGameOver();
            if (othelloModelInterface.checkGameOver()){
                JFrame end = new JFrame("Congratulations!");
                JLabel results = new JLabel();
                end.setDefaultCloseOperation(EXIT_ON_CLOSE);
                end.setVisible(true);
                end.setSize(400,400);
                if (othelloModelInterface.endGame() == CellState.BLACK){
                    results.setText("Black Wins!");
                }
                if (othelloModelInterface.endGame() == CellState.WHITE){
                    results.setText("White Wins!");
                } else {
                    results.setText("It's a Tie!");
                }
                end.add(results);
            }
        }
    }
}