package othello.controller;

import java.util.Optional;
import java.util.function.Consumer;
import othello.ai.Strategy;
import othello.model.Disc;
import othello.model.Game;
import othello.model.Position;

/**
 * The <b>Controller</b>: turns user input (a click on a square, "undo", "new game") into
 * calls on the {@link Game} model, and decides when the computer should move.
 * It knows nothing about Swing; the view supplies a scheduler for the computer's move.
 */
public final class GameController {

    /** In computer modes the human always plays this colour. */
    public static final Disc HUMAN = Disc.BLACK;

    private final Game game;
    private final Consumer<Runnable> computerScheduler;
    private Mode mode = Mode.TWO_PLAYER;
    private Strategy computer;

    /**
     * @param computerScheduler runs the computer's move later (the Swing view adds a short delay
     *                          so the player can see what happened); tests pass {@code Runnable::run}
     */
    public GameController(Game game, Consumer<Runnable> computerScheduler) {
        this.game = game;
        this.computerScheduler = computerScheduler;
    }

    public Game game() {
        return game;
    }

    public Mode mode() {
        return mode;
    }

    public void newGame(Mode newMode) {
        mode = newMode;
        computer = newMode.computer().orElse(null);
        game.reset();
    }

    /** True while it is the human's turn (always true in two-player mode). */
    public boolean acceptsInput() {
        return !game.isOver() && (computer == null || game.currentPlayer() == HUMAN);
    }

    /** Handles a click. Illegal or out-of-turn clicks are ignored and return false. */
    public boolean onSquareClicked(Position p) {
        if (!acceptsInput() || !game.isLegal(p)) {
            return false;
        }
        game.play(p);
        maybeScheduleComputer();
        return true;
    }

    /** Undo. Against the computer this rewinds to the human's previous turn. */
    public void undo() {
        if (computer == null) {
            game.undo();
            return;
        }
        // Rewind until it is the human's turn again (skipping over computer moves).
        while (game.undo()) {
            if (game.currentPlayer() == HUMAN) {
                break;
            }
        }
    }

    public Optional<String> computerName() {
        return Optional.ofNullable(computer).map(Strategy::name);
    }

    private void maybeScheduleComputer() {
        if (computer == null || game.isOver() || game.currentPlayer() == HUMAN) {
            return;
        }
        computerScheduler.accept(() -> {
            // The position may have changed (undo / new game) while we were waiting.
            if (computer == null || game.isOver() || game.currentPlayer() == HUMAN) {
                return;
            }
            game.play(computer.chooseMove(game.board(), game.currentPlayer()));
            maybeScheduleComputer(); // the human may have had to pass
        });
    }
}
