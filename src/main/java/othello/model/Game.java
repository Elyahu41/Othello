package othello.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The <b>Model</b>: one game of Othello. It owns the board, whose turn it is, passing,
 * game-over detection and undo. Views never change the board directly; they call
 * {@link #play(Position)} and get told about changes through {@link GameListener}.
 */
public final class Game {

    private Board board = Board.initial();
    private Disc current = Disc.BLACK;
    private Position lastMove;
    private List<Position> lastFlips = List.of();
    private Disc lastPassed;
    private boolean over;

    private final Deque<Snapshot> history = new ArrayDeque<>();
    private final List<GameListener> listeners = new CopyOnWriteArrayList<>();

    private record Snapshot(Board board, Disc current, Position lastMove, List<Position> lastFlips,
                            Disc lastPassed, boolean over) {
    }

    /** Read-only view of the board. Use {@link Board#copy()} to experiment with it. */
    public Board board() {
        return board;
    }

    public Disc currentPlayer() {
        return current;
    }

    public boolean isOver() {
        return over;
    }

    public Optional<Position> lastMove() {
        return Optional.ofNullable(lastMove);
    }

    public List<Position> lastFlips() {
        return lastFlips;
    }

    /** The player who had to pass after the last move, if any. */
    public Optional<Disc> lastPassed() {
        return Optional.ofNullable(lastPassed);
    }

    public int score(Disc disc) {
        return board.count(disc);
    }

    public List<Position> legalMoves() {
        return over ? List.of() : board.legalMoves(current);
    }

    public boolean isLegal(Position p) {
        return !over && board.isLegal(p, current);
    }

    /** The winner once the game is over; empty while in progress or on a draw. */
    public Optional<Disc> winner() {
        if (!over) {
            return Optional.empty();
        }
        int black = score(Disc.BLACK);
        int white = score(Disc.WHITE);
        if (black == white) {
            return Optional.empty();
        }
        return Optional.of(black > white ? Disc.BLACK : Disc.WHITE);
    }

    /**
     * Plays a disc for the current player. Afterwards the turn passes to the opponent; if the
     * opponent has no legal move they pass, and if neither player can move the game ends.
     *
     * @return the discs that were flipped
     * @throws IllegalArgumentException if the move is illegal
     * @throws IllegalStateException    if the game is already over
     */
    public List<Position> play(Position p) {
        if (over) {
            throw new IllegalStateException("The game is over");
        }
        Snapshot before = snapshot();
        List<Position> flips = board.place(p, current);
        history.push(before);

        lastMove = p;
        lastFlips = List.copyOf(flips);
        lastPassed = null;
        advanceTurn();
        fireChanged();
        return flips;
    }

    /** Undoes the last move. Returns false if there is nothing to undo. */
    public boolean undo() {
        if (history.isEmpty()) {
            return false;
        }
        restore(history.pop());
        fireChanged();
        return true;
    }

    public boolean canUndo() {
        return !history.isEmpty();
    }

    public void reset() {
        board = Board.initial();
        current = Disc.BLACK;
        lastMove = null;
        lastFlips = List.of();
        lastPassed = null;
        over = false;
        history.clear();
        fireChanged();
    }

    public void addListener(GameListener listener) {
        listeners.add(listener);
    }

    public void removeListener(GameListener listener) {
        listeners.remove(listener);
    }

    private void advanceTurn() {
        Disc next = current.opponent();
        if (board.hasLegalMove(next)) {
            current = next;
        } else if (board.hasLegalMove(current)) {
            lastPassed = next; // opponent must pass, same player goes again
        } else {
            over = true;
        }
    }

    private Snapshot snapshot() {
        return new Snapshot(board.copy(), current, lastMove, lastFlips, lastPassed, over);
    }

    private void restore(Snapshot s) {
        board = s.board();
        current = s.current();
        lastMove = s.lastMove();
        lastFlips = s.lastFlips();
        lastPassed = s.lastPassed();
        over = s.over();
    }

    private void fireChanged() {
        for (GameListener l : new ArrayList<>(listeners)) {
            l.gameChanged(this);
        }
    }

    /** Builds a game from a custom position. Intended for tests and puzzles. */
    public static Game fromPosition(Board board, Disc toMove) {
        Game game = new Game();
        game.board = board.copy();
        game.current = toMove;
        if (!game.board.hasLegalMove(toMove)) {
            game.lastMove = null;
            game.current = toMove.opponent();
            if (!game.board.hasLegalMove(game.current)) {
                game.over = true;
            }
        }
        return game;
    }
}
