package othello;

import java.awt.GraphicsEnvironment;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import othello.console.ConsoleGame;
import othello.controller.Mode;
import othello.ui.OthelloFrame;

/**
 * Starts the Swing window by default. Pass {@code --console} (or run without a display)
 * to play in the terminal instead.
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) throws IOException {
        boolean console = Arrays.asList(args).contains("--console") || GraphicsEnvironment.isHeadless();
        if (console) {
            new ConsoleGame(new BufferedReader(new InputStreamReader(System.in)), System.out).run();
            return;
        }
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
                // Fall back to the default look and feel.
            }
            new OthelloFrame(Mode.VS_HARD).setVisible(true);
        });
    }
}
