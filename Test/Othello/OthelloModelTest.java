package Othello;
//Elyahu Jacobi

import org.junit.Test;

import static org.junit.Assert.*;

public class OthelloModelTest {
    Othello.OthelloModel model = new OthelloModel();
    @Test
    public void makeMove() {
        boolean actual = model.isLocationAvailable(4, 5);
        assertEquals(true, actual);
    }

    @Test
    public void isMoveLegal() {
        boolean actual = model.isMoveLegal(4,5, CellState.WHITE);
        assertEquals(true,actual);
    }

    @Test
    public void isMoveFlippable() {
        boolean actual = model.isMoveFlippable(4,5,CellState.WHITE);
        assertEquals(true,actual);
    }

    @Test
    public void isLocationAvailable() {
        boolean actual = model.isLocationAvailable(4,5);
        assertEquals(true,actual);
    }

    @Test
    public void checkGameOver() {
        boolean actual = model.checkGameOver();
        assertEquals(false,actual);
    }

    @Test
    public void getCellState() {
        CellState cellState = model.getCellState(3,3);
        assertEquals(CellState.WHITE,cellState);
    }
}