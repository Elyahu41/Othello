package othello.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * An 8×8 Othello board and the rules for placing a disc. Pure logic: no UI, no I/O.
 * Each {@code Board} is independent, so a computer player can {@link #copy()} it and try moves.
 */
public final class Board {

    public static final int SIZE = 8;

    /** The eight compass directions as {row, col} steps. */
    private static final int[][] DIRECTIONS = {
            {-1, -1}, {-1, 0}, {-1, 1},
            {0, -1},           {0, 1},
            {1, -1},  {1, 0},  {1, 1}
    };

    private final Disc[][] cells = new Disc[SIZE][SIZE];

    private Board() {
    }

    /** The standard opening: White on d4 and e5, Black on e4 and d5. */
    public static Board initial() {
        Board board = new Board();
        int mid = SIZE / 2;
        board.cells[mid - 1][mid - 1] = Disc.WHITE;
        board.cells[mid][mid] = Disc.WHITE;
        board.cells[mid - 1][mid] = Disc.BLACK;
        board.cells[mid][mid - 1] = Disc.BLACK;
        return board;
    }

    /** An empty board, mainly for setting up positions in tests. */
    public static Board empty() {
        return new Board();
    }

    public Board copy() {
        Board copy = new Board();
        for (int r = 0; r < SIZE; r++) {
            System.arraycopy(cells[r], 0, copy.cells[r], 0, SIZE);
        }
        return copy;
    }

    public Optional<Disc> get(Position p) {
        return Optional.ofNullable(cells[p.row()][p.col()]);
    }

    public boolean isEmpty(Position p) {
        return cells[p.row()][p.col()] == null;
    }

    /** Places a disc without flipping anything. Used to build test positions. */
    public void set(Position p, Disc disc) {
        cells[p.row()][p.col()] = disc;
    }

    /** The opponent discs that would be flipped if {@code disc} were played at {@code p}. Empty means illegal. */
    public List<Position> flipsFor(Position p, Disc disc) {
        List<Position> flips = new ArrayList<>();
        if (!isEmpty(p)) {
            return flips;
        }
        for (int[] d : DIRECTIONS) {
            List<Position> line = new ArrayList<>();
            int r = p.row() + d[0];
            int c = p.col() + d[1];
            while (Position.isOnBoard(r, c) && cells[r][c] == disc.opponent()) {
                line.add(new Position(r, c));
                r += d[0];
                c += d[1];
            }
            // Only a line of opponents capped by one of our own discs is captured.
            if (!line.isEmpty() && Position.isOnBoard(r, c) && cells[r][c] == disc) {
                flips.addAll(line);
            }
        }
        return flips;
    }

    public boolean isLegal(Position p, Disc disc) {
        return !flipsFor(p, disc).isEmpty();
    }

    public List<Position> legalMoves(Disc disc) {
        List<Position> moves = new ArrayList<>();
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                Position p = new Position(r, c);
                if (isLegal(p, disc)) {
                    moves.add(p);
                }
            }
        }
        return moves;
    }

    public boolean hasLegalMove(Disc disc) {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (isLegal(new Position(r, c), disc)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Plays {@code disc} at {@code p} and flips the captured discs.
     *
     * @return the discs that were flipped
     * @throws IllegalArgumentException if the move is not legal
     */
    public List<Position> place(Position p, Disc disc) {
        List<Position> flips = flipsFor(p, disc);
        if (flips.isEmpty()) {
            throw new IllegalArgumentException(disc.displayName() + " cannot play " + p);
        }
        cells[p.row()][p.col()] = disc;
        for (Position f : flips) {
            cells[f.row()][f.col()] = disc;
        }
        return flips;
    }

    public int count(Disc disc) {
        int n = 0;
        for (Disc[] row : cells) {
            for (Disc d : row) {
                if (d == disc) {
                    n++;
                }
            }
        }
        return n;
    }
}
