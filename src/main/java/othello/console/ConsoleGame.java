package othello.console;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.stream.Collectors;
import othello.controller.GameController;
import othello.controller.Mode;
import othello.model.Board;
import othello.model.Disc;
import othello.model.Game;
import othello.model.Position;

/**
 * A second <b>View</b> on the same model and controller: plays Othello in the terminal.
 * Moves are typed in standard notation, e.g. {@code d3}.
 */
public final class ConsoleGame {

    private final BufferedReader in;
    private final PrintStream out;

    public ConsoleGame(BufferedReader in, PrintStream out) {
        this.in = in;
        this.out = out;
    }

    public void run() throws IOException {
        out.println("""
                Othello - console edition
                  1) Two players
                  2) vs Computer (Easy)
                  3) vs Computer (Hard)""");
        Mode mode = switch (prompt("Choose a mode [1-3]: ")) {
            case "2" -> Mode.VS_EASY;
            case "3" -> Mode.VS_HARD;
            case null -> null;
            default -> Mode.TWO_PLAYER;
        };
        if (mode == null) {
            return;
        }

        Game game = new Game();
        // The console runs the computer's move immediately.
        GameController controller = new GameController(game, Runnable::run);
        game.addListener(g -> g.lastPassed().ifPresent(p -> out.println(p.displayName() + " has no legal move and passes.")));
        controller.newGame(mode);
        out.println("Type a square such as d3, 'undo', or 'quit'. You are Black (B); Black moves first.\n");

        while (!game.isOver()) {
            print(game);
            List<Position> legal = game.legalMoves();
            out.println(game.currentPlayer().displayName() + " to move. Legal: "
                    + legal.stream().map(Position::toString).collect(Collectors.joining(" ")));
            String input = prompt("> ");
            if (input == null || input.equalsIgnoreCase("quit")) {
                return;
            }
            if (input.equalsIgnoreCase("undo")) {
                controller.undo();
                continue;
            }
            try {
                Disc mover = game.currentPlayer();
                if (!controller.onSquareClicked(Position.parse(input))) {
                    out.println("That isn't a legal move.");
                } else if (controller.computerName().isPresent() && mover == GameController.HUMAN) {
                    game.lastMove().filter(m -> game.board().get(m).orElse(null) != mover)
                            .ifPresent(m -> out.println("Computer plays " + m));
                }
            } catch (IllegalArgumentException e) {
                out.println(e.getMessage());
            }
        }

        print(game);
        int black = game.score(Disc.BLACK);
        int white = game.score(Disc.WHITE);
        out.println(game.winner().map(w -> w.displayName() + " wins").orElse("Draw") + ", " + black + "-" + white + ".");
    }

    private String prompt(String text) throws IOException {
        out.print(text);
        out.flush();
        String line = in.readLine();
        return line == null ? null : line.trim();
    }

    private void print(Game game) {
        out.println("    a b c d e f g h");
        for (int r = 0; r < Board.SIZE; r++) {
            StringBuilder line = new StringBuilder("  ").append(r + 1).append(' ');
            for (int c = 0; c < Board.SIZE; c++) {
                Position p = new Position(r, c);
                char symbol = game.board().get(p).map(Disc::symbol)
                        .orElse(game.isLegal(p) ? '*' : '.');
                line.append(symbol).append(' ');
            }
            out.println(line);
        }
        out.printf("  Black %d | White %d%n", game.score(Disc.BLACK), game.score(Disc.WHITE));
    }
}
