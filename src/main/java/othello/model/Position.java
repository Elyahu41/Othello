package othello.model;

/**
 * A square on the board. Rows and columns are zero-based internally, but positions are
 * written in standard Othello notation: column letter + row number, e.g. {@code d3}.
 */
public record Position(int row, int col) {

    public Position {
        if (!isOnBoard(row, col)) {
            throw new IllegalArgumentException("Off the board: row=" + row + ", col=" + col);
        }
    }

    public static boolean isOnBoard(int row, int col) {
        return row >= 0 && row < Board.SIZE && col >= 0 && col < Board.SIZE;
    }

    /** Parses notation such as {@code "d3"} or {@code "D3"}. */
    public static Position parse(String text) {
        String s = text == null ? "" : text.trim().toLowerCase();
        if (s.length() != 2) {
            throw new IllegalArgumentException("Expected a square like d3, got: " + text);
        }
        int col = s.charAt(0) - 'a';
        int row = s.charAt(1) - '1';
        if (!isOnBoard(row, col)) {
            throw new IllegalArgumentException("Not a square on the board: " + text);
        }
        return new Position(row, col);
    }

    @Override
    public String toString() {
        return "" + (char) ('a' + col) + (row + 1);
    }
}
