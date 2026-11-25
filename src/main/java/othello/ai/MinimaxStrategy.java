package othello.ai;

import java.util.List;
import othello.model.Board;
import othello.model.Disc;
import othello.model.Position;

/**
 * "Hard" opponent: looks several moves ahead with minimax and alpha–beta pruning, scoring
 * positions by square weights plus mobility (how many moves each side has).
 */
public final class MinimaxStrategy implements Strategy {

    private static final int WIN = 1_000_000;

    private final int depth;

    public MinimaxStrategy(int depth) {
        if (depth < 1) {
            throw new IllegalArgumentException("depth must be at least 1");
        }
        this.depth = depth;
    }

    @Override
    public Position chooseMove(Board board, Disc me) {
        List<Position> moves = board.legalMoves(me);
        if (moves.isEmpty()) {
            throw new IllegalStateException(me.displayName() + " has no legal move");
        }
        Position best = moves.getFirst();
        int bestScore = Integer.MIN_VALUE;
        int alpha = -WIN - 1;
        for (Position move : moves) {
            Board next = board.copy();
            next.place(move, me);
            int score = search(next, me.opponent(), me, depth - 1, alpha, WIN + 1);
            if (score > bestScore) {
                bestScore = score;
                best = move;
            }
            alpha = Math.max(alpha, score);
        }
        return best;
    }

    /** Score of {@code board} for {@code me} when it is {@code toMove}'s turn. */
    private int search(Board board, Disc toMove, Disc me, int remaining, int alpha, int beta) {
        List<Position> moves = board.legalMoves(toMove);
        if (moves.isEmpty()) {
            if (!board.hasLegalMove(toMove.opponent())) {
                int diff = board.count(me) - board.count(me.opponent());
                return Integer.signum(diff) * WIN + diff; // game over: win/loss dominates
            }
            return search(board, toMove.opponent(), me, remaining, alpha, beta); // pass
        }
        if (remaining == 0) {
            return evaluate(board, me);
        }

        boolean maximizing = toMove == me;
        int best = maximizing ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        for (Position move : moves) {
            Board next = board.copy();
            next.place(move, toMove);
            int score = search(next, toMove.opponent(), me, remaining - 1, alpha, beta);
            if (maximizing) {
                best = Math.max(best, score);
                alpha = Math.max(alpha, score);
            } else {
                best = Math.min(best, score);
                beta = Math.min(beta, score);
            }
            if (beta <= alpha) {
                break;
            }
        }
        return best;
    }

    private static int evaluate(Board board, Disc me) {
        int mobility = board.legalMoves(me).size() - board.legalMoves(me.opponent()).size();
        return Weights.evaluate(board, me) + 5 * mobility;
    }

    @Override
    public String name() {
        return "Hard";
    }
}
