package Othello;
public class Main {
// Elyahu Jacobi
    public static void main(String[] args) {
        OthelloModelInterface othelloModel = new OthelloModel();
        new OthelloWindow(othelloModel);
    }
}