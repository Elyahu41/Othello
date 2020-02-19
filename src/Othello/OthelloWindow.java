package Othello;
//Elyahu Jacobi
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.atomic.AtomicBoolean;

public class OthelloWindow extends JFrame {
    private int GRID_SIZE = 8;
    private AtomicBoolean isBlackTurn = new AtomicBoolean(true);
    private JButton[][] buttons = new JButton[GRID_SIZE][GRID_SIZE];

    public OthelloWindow(OthelloModelInterface othelloModelInterface) {
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
                b.addActionListener(new ButtonPressedListener(b, othelloModelInterface));
                setMiddlePieces(row, col, b);
            }
        }
        this.setVisible(true);
    }

    private void setMiddlePieces(int row, int col, JButton b) {
        b.setBackground(new Color(39,134,50));
        if (row == 3 && col == 3) {
            b.setBackground(Color.WHITE);
        } else if (row == 4 && col == 3) {
            b.setBackground(Color.BLACK);
        } else if (row == 3 && col == 4) {
            b.setBackground(Color.BLACK);
        } else if (row == 4 && col == 4) {
            b.setBackground(Color.WHITE);
        } else {
            b.setBackground(new Color(39,134,50));
        }
    }

    private OthelloModelInterface model;
    class ButtonPressedListener implements ActionListener {
        private JButton b;
        ButtonPressedListener(JButton b, OthelloModelInterface othelloModelInterface) {
            model = othelloModelInterface;
            this.b = b;
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            int clickedRow = Integer.parseInt(String.valueOf(b.getName().charAt(0)));
            int clickedCol = Integer.parseInt(String.valueOf(b.getName().charAt(1)));

            if (isBlackTurn.get() && model.isMoveLegal(clickedRow, clickedCol, CellState.BLACK)) {
                model.makeMove(clickedRow, clickedCol, CellState.BLACK);
                update();
                togglePlayer();
            } else if (model.isMoveLegal(clickedRow, clickedCol, CellState.WHITE)) {
                model.makeMove(clickedRow, clickedCol, CellState.WHITE);
                update();
                togglePlayer();
            }
        }

        private void togglePlayer() {
            if (isBlackTurn.get()) {
                isBlackTurn.set(false);
            } else {
                isBlackTurn.set(true);
            }
        }

        private void update() {
            for (int i = 0; i < GRID_SIZE; i++) {
                for (int j = 0; j < GRID_SIZE; j++) {
                    JButton b = buttons[i][j];
                    CellState cellState = model.getCellState(i, j);

                    if (cellState == CellState.WHITE) {
                        b.setBackground(Color.WHITE);
                    } else if (cellState == CellState.BLACK) {
                        b.setBackground(Color.BLACK);
                    }
                }
                revalidate();
                model.checkGameOver();
            }
        }
    }
}