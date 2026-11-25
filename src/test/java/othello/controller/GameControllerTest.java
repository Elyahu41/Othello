package othello.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import othello.model.Disc;
import othello.model.Game;
import othello.model.Position;

class GameControllerTest {

    private static Position at(String s) {
        return Position.parse(s);
    }

    @Test
    void twoPlayerModeAcceptsBothColours() {
        GameController controller = new GameController(new Game(), Runnable::run);
        controller.newGame(Mode.TWO_PLAYER);

        assertTrue(controller.onSquareClicked(at("d3")));
        assertTrue(controller.acceptsInput());
        assertEquals(Disc.WHITE, controller.game().currentPlayer());
    }

    @Test
    void illegalClickIsIgnored() {
        GameController controller = new GameController(new Game(), Runnable::run);
        controller.newGame(Mode.TWO_PLAYER);

        assertFalse(controller.onSquareClicked(at("a1")));
        assertEquals(Disc.BLACK, controller.game().currentPlayer());
    }

    @Test
    void computerRepliesAfterHumanMove() {
        GameController controller = new GameController(new Game(), Runnable::run);
        controller.newGame(Mode.VS_EASY);

        controller.onSquareClicked(at("d3"));

        Game game = controller.game();
        assertEquals(Disc.BLACK, game.currentPlayer());
        assertEquals(6, game.score(Disc.BLACK) + game.score(Disc.WHITE));
    }

    @Test
    void humanCannotMoveWhileComputerIsThinking() {
        List<Runnable> pending = new ArrayList<>();
        GameController controller = new GameController(new Game(), pending::add);
        controller.newGame(Mode.VS_HARD);

        controller.onSquareClicked(at("d3"));

        assertFalse(controller.acceptsInput());
        assertFalse(controller.onSquareClicked(at("c3")));
        pending.removeFirst().run();
        assertTrue(controller.acceptsInput());
    }

    @Test
    void undoAgainstComputerRewindsToHumansTurn() {
        GameController controller = new GameController(new Game(), Runnable::run);
        controller.newGame(Mode.VS_EASY);
        controller.onSquareClicked(at("d3"));

        controller.undo();

        Game game = controller.game();
        assertEquals(Disc.BLACK, game.currentPlayer());
        assertEquals(2, game.score(Disc.BLACK));
        assertEquals(2, game.score(Disc.WHITE));
    }

    @Test
    void staleComputerMoveIsDroppedAfterNewGame() {
        List<Runnable> pending = new ArrayList<>();
        GameController controller = new GameController(new Game(), pending::add);
        controller.newGame(Mode.VS_EASY);
        controller.onSquareClicked(at("d3"));

        controller.newGame(Mode.VS_EASY);
        pending.removeFirst().run();

        assertEquals(4, controller.game().score(Disc.BLACK) + controller.game().score(Disc.WHITE));
    }
}
