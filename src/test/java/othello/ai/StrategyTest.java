package othello.ai;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.Test;
import othello.model.Board;
import othello.model.Disc;
import othello.model.Game;
import othello.model.Position;

class StrategyTest {

    static Stream<Strategy> strategies() {
        return Stream.of(new GreedyStrategy(), new MinimaxStrategy(1), new MinimaxStrategy(3));
    }

    private static Position at(String s) {
        return Position.parse(s);
    }

    @ParameterizedTest
    @MethodSource("strategies")
    void alwaysPicksALegalMove_ThroughAWholeGame(Strategy strategy) {
        Game game = new Game();
        while (!game.isOver()) {
            Position move = strategy.chooseMove(game.board(), game.currentPlayer());
            assertTrue(game.isLegal(move), strategy.name() + " chose illegal " + move);
            game.play(move);
        }
        assertEquals(64, game.score(Disc.BLACK) + game.score(Disc.WHITE)
                + (int) emptySquares(game.board()));
    }

    @ParameterizedTest
    @MethodSource("strategies")
    void doesNotModifyTheBoardItIsGiven(Strategy strategy) {
        Board board = Board.initial();
        strategy.chooseMove(board, Disc.BLACK);
        assertEquals(2, board.count(Disc.BLACK));
        assertEquals(2, board.count(Disc.WHITE));
    }

    @Test
    void greedyTakesTheMoveThatFlipsTheMost() {
        Board board = Board.empty();
        // Playing a1 flips one disc; playing h1 flips three.
        board.set(at("b1"), Disc.WHITE);
        board.set(at("c1"), Disc.BLACK);
        board.set(at("g1"), Disc.WHITE);
        board.set(at("f1"), Disc.WHITE);
        board.set(at("e1"), Disc.WHITE);
        board.set(at("d1"), Disc.BLACK);

        assertEquals(at("h1"), new GreedyStrategy().chooseMove(board, Disc.BLACK));
    }

    @ParameterizedTest
    @EnumSource(Disc.class)
    void hardBeatsEasyWithEitherColour(Disc hardColour) {
        Strategy hard = new MinimaxStrategy(4);
        Strategy easy = new GreedyStrategy();
        Game game = new Game();
        while (!game.isOver()) {
            Strategy s = game.currentPlayer() == hardColour ? hard : easy;
            game.play(s.chooseMove(game.board(), game.currentPlayer()));
        }
        assertEquals(java.util.Optional.of(hardColour), game.winner(),
                "Hard %d – Easy %d".formatted(game.score(hardColour), game.score(hardColour.opponent())));
    }

    private static long emptySquares(Board board) {
        long empty = 0;
        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {
                if (board.isEmpty(new Position(r, c))) {
                    empty++;
                }
            }
        }
        return empty;
    }
}
