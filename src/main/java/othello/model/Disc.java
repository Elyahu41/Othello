package othello.model;

/** The two colours a disc can show. An empty square is represented by {@code null} on the {@link Board}. */
public enum Disc {
    BLACK, WHITE;

    public Disc opponent() {
        return this == BLACK ? WHITE : BLACK;
    }

    public String displayName() {
        return this == BLACK ? "Black" : "White";
    }

    /** One-character symbol used by the console view. */
    public char symbol() {
        return this == BLACK ? 'B' : 'W';
    }
}
