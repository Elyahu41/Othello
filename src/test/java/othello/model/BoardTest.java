package othello.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class BoardTest {

    private static Position at(String s) {
        return Position.parse(s);
    }

    @Test
    void initialBoardHasStandardOpening() {
        Board board = Board.initial();

        assertEquals(Disc.WHITE, board.get(at("d4")).orElseThrow());
        assertEquals(Disc.WHITE, board.get(at("e5")).orElseThrow());
        assertEquals(Disc.BLACK, board.get(at("e4")).orElseThrow());
        assertEquals(Disc.BLACK, board.get(at("d5")).orElseThrow());
        assertEquals(2, board.count(Disc.BLACK));
        assertEquals(2, board.count(Disc.WHITE));
    }

    @Test
    void blackHasFourOpeningMoves() {
        Set<Position> moves = Set.copyOf(Board.initial().legalMoves(Disc.BLACK));

        assertEquals(Set.of(at("d3"), at("c4"), at("f5"), at("e6")), moves);
    }

    @Test
    void placingFlipsCapturedDisc() {
        Board board = Board.initial();

        List<Position> flipped = board.place(at("d3"), Disc.BLACK);

        assertEquals(List.of(at("d4")), flipped);
        assertEquals(4, board.count(Disc.BLACK));
        assertEquals(1, board.count(Disc.WHITE));
    }

    @Test
    void flipsInSeveralDirectionsAtOnce() {
        Board board = Board.empty();
        // Black at a1 and e5 with white discs between them on two lines meeting at e1/a5... use a star:
        board.set(at("a1"), Disc.BLACK);
        board.set(at("b2"), Disc.WHITE);   // diagonal towards a1
        board.set(at("c1"), Disc.BLACK);
        board.set(at("c2"), Disc.WHITE);   // vertical towards c1
        board.set(at("e1"), Disc.BLACK);
        board.set(at("d2"), Disc.WHITE);   // diagonal towards e1

        List<Position> flipped = board.place(at("c3"), Disc.BLACK);

        assertEquals(Set.of(at("b2"), at("c2"), at("d2")), Set.copyOf(flipped));
        assertEquals(0, board.count(Disc.WHITE));
    }

    @Test
    void lineMustBeCappedByOwnDisc() {
        Board board = Board.empty();
        board.set(at("a1"), Disc.WHITE);
        board.set(at("b1"), Disc.WHITE); // runs into the edge, no black cap

        assertFalse(board.isLegal(at("c1"), Disc.BLACK));
    }

    @Test
    void cornerIsNotLegalJustBecauseItIsNextToDiscs() {
        // Regression for the 2020 bug: corners could be taken whenever a disc was adjacent.
        Board board = Board.empty();
        board.set(at("b2"), Disc.WHITE);
        board.set(at("b1"), Disc.WHITE);
        board.set(at("a2"), Disc.WHITE);

        assertFalse(board.isLegal(at("a1"), Disc.BLACK));
    }

    @Test
    void occupiedSquareIsNeverLegal() {
        assertFalse(Board.initial().isLegal(at("d4"), Disc.BLACK));
    }

    @Test
    void illegalPlaceThrowsAndLeavesBoardUnchanged() {
        Board board = Board.initial();

        assertThrows(IllegalArgumentException.class, () -> board.place(at("a1"), Disc.BLACK));
        assertTrue(board.isEmpty(at("a1")));
        assertEquals(2, board.count(Disc.BLACK));
    }

    @Test
    void copyIsIndependent() {
        Board board = Board.initial();
        Board copy = board.copy();

        copy.place(at("d3"), Disc.BLACK);

        assertEquals(2, board.count(Disc.BLACK));
        assertEquals(4, copy.count(Disc.BLACK));
    }

    @ParameterizedTest
    @ValueSource(strings = {"a1", "h8", "d3", "H1", " e6 "})
    void positionNotationRoundTrips(String text) {
        assertEquals(text.trim().toLowerCase(), Position.parse(text).toString());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "i1", "a9", "a0", "abc", "33"})
    void invalidNotationIsRejected(String text) {
        assertThrows(IllegalArgumentException.class, () -> Position.parse(text));
    }
}
