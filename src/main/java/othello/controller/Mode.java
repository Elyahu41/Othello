package othello.controller;

import java.util.Optional;
import othello.ai.GreedyStrategy;
import othello.ai.MinimaxStrategy;
import othello.ai.Strategy;

/** Who is playing. In the computer modes the human is Black and moves first. */
public enum Mode {
    TWO_PLAYER("Two players"),
    VS_EASY("vs Computer (Easy)"),
    VS_HARD("vs Computer (Hard)");

    private final String label;

    Mode(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    public Optional<Strategy> computer() {
        return switch (this) {
            case TWO_PLAYER -> Optional.empty();
            case VS_EASY -> Optional.of(new GreedyStrategy());
            case VS_HARD -> Optional.of(new MinimaxStrategy(4));
        };
    }
}
