package othello.ai;

import othello.model.Board;
import othello.model.Disc;
import othello.model.Position;

/**
 * Classic positional weights: corners are gold, the squares next to corners are dangerous
 * because they hand the corner to the opponent, and edges are generally good.
 */
final class Weights {

    private static final int[][] TABLE = {
            {100, -20, 10,  5,  5, 10, -20, 100},
            {-20, -50, -2, -2, -2, -2, -50, -20},
            { 10,  -2,  1,  1,  1,  1,  -2,  10},
            {  5,  -2,  1,  0,  0,  1,  -2,   5},
            {  5,  -2,  1,  0,  0,  1,  -2,   5},
            { 10,  -2,  1,  1,  1,  1,  -2,  10},
            {-20, -50, -2, -2, -2, -2, -50, -20},
            {100, -20, 10,  5,  5, 10, -20, 100}
    };

    private Weights() {
    }

    static int of(Position p) {
        return TABLE[p.row()][p.col()];
    }

    /** Positional score of the whole board from {@code me}'s point of view. */
    static int evaluate(Board board, Disc me) {
        int score = 0;
        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {
                Position p = new Position(r, c);
                Disc d = board.get(p).orElse(null);
                if (d == me) {
                    score += TABLE[r][c];
                } else if (d == me.opponent()) {
                    score -= TABLE[r][c];
                }
            }
        }
        return score;
    }
}
