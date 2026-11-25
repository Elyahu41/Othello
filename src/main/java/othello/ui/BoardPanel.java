package othello.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RadialGradientPaint;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import java.util.Optional;
import javax.swing.JPanel;
import othello.controller.GameController;
import othello.model.Board;
import othello.model.Disc;
import othello.model.Game;
import othello.model.Position;

/** Part of the <b>View</b>: draws the board and forwards clicks to the controller. */
final class BoardPanel extends JPanel {

    private static final Color FELT_LIGHT = new Color(0x2E8B57);
    private static final Color FELT_DARK = new Color(0x1F6B43);
    private static final Color GRID = new Color(0x154D30);
    private static final Color FRAME = new Color(0x3B2A1E);
    private static final Color HINT = new Color(255, 255, 255, 70);
    private static final Color LAST_MOVE = new Color(0xF87171);
    private static final int MARGIN = 28;

    private final GameController controller;
    private Position hover;
    private boolean showHints = true;

    BoardPanel(GameController controller) {
        this.controller = controller;
        setPreferredSize(new Dimension(560, 560));
        setOpaque(false);

        MouseAdapter mouse = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                toPosition(e.getX(), e.getY()).ifPresent(controller::onSquareClicked);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                Position p = toPosition(e.getX(), e.getY()).orElse(null);
                boolean clickable = p != null && controller.acceptsInput() && controller.game().isLegal(p);
                setCursor(Cursor.getPredefinedCursor(clickable ? Cursor.HAND_CURSOR : Cursor.DEFAULT_CURSOR));
                if (!java.util.Objects.equals(p, hover)) {
                    hover = p;
                    repaint();
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hover = null;
                repaint();
            }
        };
        addMouseListener(mouse);
        addMouseMotionListener(mouse);
    }

    void setShowHints(boolean showHints) {
        this.showHints = showHints;
        repaint();
    }

    private double cellSize() {
        return (Math.min(getWidth(), getHeight()) - 2.0 * MARGIN) / Board.SIZE;
    }

    private double originX() {
        return (getWidth() - cellSize() * Board.SIZE) / 2;
    }

    private double originY() {
        return (getHeight() - cellSize() * Board.SIZE) / 2;
    }

    private Optional<Position> toPosition(int x, int y) {
        double cell = cellSize();
        int col = (int) Math.floor((x - originX()) / cell);
        int row = (int) Math.floor((y - originY()) / cell);
        return Position.isOnBoard(row, col) ? Optional.of(new Position(row, col)) : Optional.empty();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        Game game = controller.game();
        double cell = cellSize();
        double x0 = originX();
        double y0 = originY();
        double size = cell * Board.SIZE;

        // Wooden frame
        g.setColor(FRAME);
        g.fill(new RoundRectangle2D.Double(x0 - MARGIN + 4, y0 - MARGIN + 4, size + 2 * MARGIN - 8, size + 2 * MARGIN - 8, 24, 24));

        // Felt
        g.setPaint(new GradientPaint((float) x0, (float) y0, FELT_LIGHT, (float) (x0 + size), (float) (y0 + size), FELT_DARK));
        g.fill(new RoundRectangle2D.Double(x0, y0, size, size, 6, 6));

        // Grid lines
        g.setColor(GRID);
        g.setStroke(new BasicStroke(1.5f));
        for (int i = 1; i < Board.SIZE; i++) {
            g.draw(new java.awt.geom.Line2D.Double(x0 + i * cell, y0, x0 + i * cell, y0 + size));
            g.draw(new java.awt.geom.Line2D.Double(x0, y0 + i * cell, x0 + size, y0 + i * cell));
        }

        // Star points
        for (int r : new int[]{2, 6}) {
            for (int c : new int[]{2, 6}) {
                double d = cell * 0.12;
                g.fill(new Ellipse2D.Double(x0 + c * cell - d / 2, y0 + r * cell - d / 2, d, d));
            }
        }

        // Coordinates
        g.setColor(new Color(0xE7D7C1));
        g.setFont(getFont().deriveFont(java.awt.Font.BOLD, 13f));
        var fm = g.getFontMetrics();
        for (int i = 0; i < Board.SIZE; i++) {
            String col = String.valueOf((char) ('a' + i));
            String row = String.valueOf(i + 1);
            g.drawString(col, (float) (x0 + i * cell + cell / 2 - fm.stringWidth(col) / 2.0), (float) (y0 - 9));
            g.drawString(row, (float) (x0 - 18), (float) (y0 + i * cell + cell / 2 + fm.getAscent() / 2.0 - 2));
        }

        // Discs
        Board board = game.board();
        for (int r = 0; r < Board.SIZE; r++) {
            for (int c = 0; c < Board.SIZE; c++) {
                Position p = new Position(r, c);
                double cx = x0 + c * cell + cell / 2;
                double cy = y0 + r * cell + cell / 2;
                board.get(p).ifPresent(disc -> drawDisc(g, disc, cx, cy, cell * 0.40));
            }
        }

        // Last move marker
        game.lastMove().ifPresent(p -> {
            double cx = x0 + p.col() * cell + cell / 2;
            double cy = y0 + p.row() * cell + cell / 2;
            double d = cell * 0.14;
            g.setColor(LAST_MOVE);
            g.fill(new Ellipse2D.Double(cx - d / 2, cy - d / 2, d, d));
        });

        // Legal-move hints for the human
        if (controller.acceptsInput()) {
            List<Position> legal = game.legalMoves();
            for (Position p : legal) {
                double cx = x0 + p.col() * cell + cell / 2;
                double cy = y0 + p.row() * cell + cell / 2;
                if (p.equals(hover)) {
                    // Ghost disc preview
                    Graphics2D ghost = (Graphics2D) g.create();
                    ghost.setComposite(java.awt.AlphaComposite.getInstance(java.awt.AlphaComposite.SRC_OVER, 0.45f));
                    drawDisc(ghost, game.currentPlayer(), cx, cy, cell * 0.40);
                    ghost.dispose();
                } else if (showHints) {
                    double d = cell * 0.22;
                    g.setColor(HINT);
                    g.fill(new Ellipse2D.Double(cx - d / 2, cy - d / 2, d, d));
                }
            }
        }
        g.dispose();
    }

    static void drawDisc(Graphics2D g, Disc disc, double cx, double cy, double radius) {
        // Shadow
        g.setColor(new Color(0, 0, 0, 70));
        g.fill(new Ellipse2D.Double(cx - radius + 2, cy - radius + 4, radius * 2, radius * 2));

        Color[] colors = disc == Disc.BLACK
                ? new Color[]{new Color(0x5A5A5A), new Color(0x161616), new Color(0x050505)}
                : new Color[]{Color.WHITE, new Color(0xE9E9E9), new Color(0xBDBDBD)};
        g.setPaint(new RadialGradientPaint(
                new Point2D.Double(cx - radius * 0.35, cy - radius * 0.35), (float) (radius * 1.6),
                new float[]{0f, 0.55f, 1f}, colors));
        g.fill(new Ellipse2D.Double(cx - radius, cy - radius, radius * 2, radius * 2));
    }
}
