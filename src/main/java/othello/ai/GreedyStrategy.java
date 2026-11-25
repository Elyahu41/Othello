package othello.ai;

import java.util.Comparator;
import othello.model.Board;
import othello.model.Disc;
import othello.model.Position;

/**
 * "Easy" opponent: plays whichever move flips the most discs right now. Ties go to the
 * better-placed square. This is the corrected version of the 2020 greedy AI, which counted
 * neighbouring discs instead of discs actually captured.
 */
public final class GreedyStrategy implements Strategy {

    @Override
    public Position chooseMove(Board board, Disc me) {
        return board.legalMoves(me).stream()
                .max(Comparator.<Position>comparingInt(p -> board.flipsFor(p, me).size())
                        .thenComparingInt(Weights::of))
                .orElseThrow(() -> new IllegalStateException(me.displayName() + " has no legal move"));
    }

    @Override
    public String name() {
        return "Easy";
    }
}
