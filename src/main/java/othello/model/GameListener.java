package othello.model;

/** Observer for model changes. Views register one so they redraw whenever the game changes. */
@FunctionalInterface
public interface GameListener {
    void gameChanged(Game game);
}
