package othello.ai;

import othello.model.Board;
import othello.model.Disc;
import othello.model.Position;

/** A computer player. Given a position and a colour, it picks one of the legal moves. */
public interface Strategy {

    /**
     * @param board a board where {@code me} has at least one legal move. Implementations must not modify it.
     */
    Position chooseMove(Board board, Disc me);

    String name();
}
