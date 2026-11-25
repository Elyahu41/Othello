package othello.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class GameTest {

    private static Position at(String s) {
        return Position.parse(s);
    }

    @Test
    void blackMovesFirstThenTurnsAlternate() {
        Game game = new Game();
        assertEquals(Disc.BLACK, game.currentPlayer());

        game.play(at("d3"));
        assertEquals(Disc.WHITE, game.currentPlayer());

        game.play(at("c3"));
        assertEquals(Disc.BLACK, game.currentPlayer());
    }

    @Test
    void illegalMoveIsRejected() {
        Game game = new Game();

        assertThrows(IllegalArgumentException.class, () -> game.play(at("a1")));
        assertEquals(Disc.BLACK, game.currentPlayer());
    }

    @Test
    void playerWithNoMovesPasses() {
        // White to move has exactly one move (c1, flipping b1). Afterwards Black has no move but White does.
        Board board = Board.empty();
        board.set(at("a1"), Disc.WHITE);
        board.set(at("b1"), Disc.BLACK);
        board.set(at("h8"), Disc.WHITE);
        board.set(at("h7"), Disc.BLACK);
        Game game = Game.fromPosition(board, Disc.WHITE);

        game.play(at("c1"));

        assertEquals(Optional.of(Disc.BLACK), game.lastPassed());
        assertEquals(Disc.WHITE, game.currentPlayer());
        assertFalse(game.isOver());
    }

    @Test
    void gameEndsWhenNeitherPlayerCanMove_EvenIfBoardIsNotFull() {
        // The 2020 version only ended the game when all 64 squares were filled.
        Board board = Board.empty();
        board.set(at("a1"), Disc.WHITE);
        board.set(at("b1"), Disc.BLACK);
        Game game = Game.fromPosition(board, Disc.WHITE);

        game.play(at("c1")); // White wipes out Black

        assertTrue(game.isOver());
        assertEquals(Optional.of(Disc.WHITE), game.winner());
        assertEquals(3, game.score(Disc.WHITE));
        assertTrue(game.legalMoves().isEmpty());
        assertThrows(IllegalStateException.class, () -> game.play(at("d1")));
    }

    @Test
    void drawHasNoWinner() {
        Board board = Board.empty();
        board.set(at("a1"), Disc.WHITE);
        board.set(at("h8"), Disc.BLACK);
        Game game = Game.fromPosition(board, Disc.BLACK);

        assertTrue(game.isOver());
        assertEquals(Optional.empty(), game.winner());
    }

    @Test
    void undoRestoresPreviousPosition() {
        Game game = new Game();
        game.play(at("d3"));

        assertTrue(game.undo());

        assertEquals(Disc.BLACK, game.currentPlayer());
        assertEquals(2, game.score(Disc.BLACK));
        assertTrue(game.lastMove().isEmpty());
        assertFalse(game.undo());
    }

    @Test
    void listenersAreNotifiedOnEveryChange() {
        Game game = new Game();
        AtomicInteger calls = new AtomicInteger();
        game.addListener(g -> calls.incrementAndGet());

        game.play(at("d3"));
        game.undo();
        game.reset();

        assertEquals(3, calls.get());
    }

    @Test
    void lastMoveAndFlipsAreTracked() {
        Game game = new Game();

        game.play(at("f5"));

        assertEquals(Optional.of(at("f5")), game.lastMove());
        assertEquals(1, game.lastFlips().size());
    }
}
