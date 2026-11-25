package othello.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import othello.controller.GameController;
import othello.controller.Mode;
import othello.model.Disc;
import othello.model.Game;

/**
 * The <b>View</b>: a window with a score bar, the board and a status line. It listens to the
 * model and redraws itself; every user action goes through the {@link GameController}.
 */
public final class OthelloFrame extends JFrame {

    private static final Color BACKGROUND = new Color(0x1B1F24);
    private static final Color TEXT = new Color(0xE6EDF3);
    private static final Color MUTED = new Color(0x8B949E);
    private static final int COMPUTER_DELAY_MS = 450;

    private final GameController controller;
    private final BoardPanel boardPanel;
    private final JLabel blackScore = scoreLabel(Disc.BLACK);
    private final JLabel whiteScore = scoreLabel(Disc.WHITE);
    private final JLabel turnLabel = new JLabel("", SwingConstants.CENTER);
    private final JLabel statusLabel = new JLabel(" ", SwingConstants.CENTER);
    private JMenuItem undoItem;

    public OthelloFrame(Mode initialMode) {
        super("Othello");
        Game game = new Game();
        controller = new GameController(game, task -> {
            Timer timer = new Timer(COMPUTER_DELAY_MS, e -> task.run());
            timer.setRepeats(false);
            timer.start();
        });
        boardPanel = new BoardPanel(controller);

        game.addListener(g -> refresh());

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(BACKGROUND);
        setLayout(new BorderLayout());
        add(buildScoreBar(), BorderLayout.NORTH);
        add(boardPanel, BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);
        setJMenuBar(buildMenu(initialMode));

        controller.newGame(initialMode);
        pack();
        setMinimumSize(new Dimension(420, 500));
        setLocationRelativeTo(null);
    }

    private JPanel buildScoreBar() {
        JPanel bar = new JPanel();
        bar.setBackground(BACKGROUND);
        bar.setLayout(new BoxLayout(bar, BoxLayout.X_AXIS));
        bar.setBorder(BorderFactory.createEmptyBorder(14, 24, 0, 24));
        turnLabel.setForeground(TEXT);
        turnLabel.setFont(turnLabel.getFont().deriveFont(Font.BOLD, 17f));
        bar.add(blackScore);
        bar.add(Box.createHorizontalGlue());
        bar.add(turnLabel);
        bar.add(Box.createHorizontalGlue());
        bar.add(whiteScore);
        return bar;
    }

    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(BACKGROUND);
        bar.setBorder(BorderFactory.createEmptyBorder(0, 16, 14, 16));
        statusLabel.setForeground(MUTED);
        statusLabel.setFont(statusLabel.getFont().deriveFont(14f));
        bar.add(statusLabel, BorderLayout.CENTER);
        return bar;
    }

    private JMenuBar buildMenu(Mode initialMode) {
        int shortcut = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx();
        JMenuBar menuBar = new JMenuBar();

        JMenu gameMenu = new JMenu("Game");
        JMenuItem newGame = new JMenuItem("New game");
        newGame.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, shortcut));
        newGame.addActionListener(e -> controller.newGame(controller.mode()));
        gameMenu.add(newGame);

        undoItem = new JMenuItem("Undo");
        undoItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, shortcut));
        undoItem.addActionListener(e -> controller.undo());
        gameMenu.add(undoItem);
        gameMenu.addSeparator();

        ButtonGroup group = new ButtonGroup();
        for (Mode mode : Mode.values()) {
            JRadioButtonMenuItem item = new JRadioButtonMenuItem(mode.label(), mode == initialMode);
            item.addActionListener(e -> controller.newGame(mode));
            group.add(item);
            gameMenu.add(item);
        }
        gameMenu.addSeparator();

        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(e -> dispose());
        gameMenu.add(exit);

        JMenu view = new JMenu("View");
        JCheckBoxMenuItem hints = new JCheckBoxMenuItem("Show legal moves", true);
        hints.addActionListener(e -> boardPanel.setShowHints(hints.isSelected()));
        view.add(hints);

        menuBar.add(gameMenu);
        menuBar.add(view);
        return menuBar;
    }

    private void refresh() {
        Game game = controller.game();
        blackScore.setText(String.valueOf(game.score(Disc.BLACK)));
        whiteScore.setText(String.valueOf(game.score(Disc.WHITE)));

        if (game.isOver()) {
            turnLabel.setText(game.winner().map(w -> w.displayName() + " wins!").orElse("It's a draw!"));
            statusLabel.setText("Game over · %d – %d · Ctrl+N for a new game"
                    .formatted(game.score(Disc.BLACK), game.score(Disc.WHITE)));
        } else {
            Disc turn = game.currentPlayer();
            boolean computerTurn = controller.computerName().isPresent() && turn != GameController.HUMAN;
            turnLabel.setText(computerTurn ? "Computer is thinking…" : turn.displayName() + " to move");
            String status = game.lastPassed()
                    .map(p -> p.displayName() + " has no legal move and passes.")
                    .orElse(controller.mode().label() + " · " + game.legalMoves().size() + " legal moves");
            statusLabel.setText(status);
        }
        undoItem.setEnabled(game.canUndo());
        boardPanel.repaint();
    }

    private static JLabel scoreLabel(Disc disc) {
        JLabel label = new JLabel("2", new DiscIcon(disc), disc == Disc.BLACK ? SwingConstants.LEFT : SwingConstants.RIGHT);
        label.setForeground(TEXT);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 22f));
        label.setIconTextGap(10);
        if (disc == Disc.WHITE) {
            label.setHorizontalTextPosition(SwingConstants.LEFT);
        }
        return label;
    }

    /** Small disc icon used next to the scores. */
    private record DiscIcon(Disc disc) implements Icon {
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            BoardPanel.drawDisc(g2, disc, x + 13, y + 12, 11);
            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return 28;
        }

        @Override
        public int getIconHeight() {
            return 28;
        }
    }
}
