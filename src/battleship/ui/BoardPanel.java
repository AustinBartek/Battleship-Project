package battleship.ui;

import battleship.board.Board;
import battleship.board.graphics.BoardAnimation;
import battleship.board.graphics.BoardDrawer;
import battleship.board.graphics.BoardAnimation.AnimationType;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.BevelBorder;

public class BoardPanel extends JPanel {
    private Board drawBoard;
    private JPanel displayPanel;
    private final BattleshipWindow owner;
    private final boolean opponentGame;
    private final ArrayList<BoardAnimation> animations;
    private final Semaphore animationWaiter;

    private final Runnable graphicsUpdater = () -> {
        while (true) {
            displayPanel.repaint();
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
            }
        }
    };
    private final Thread graphicsThread = new Thread(graphicsUpdater);

    public BoardPanel(Board board, boolean opponentGame, BattleshipWindow window) {
        owner = window;

        animationWaiter = new Semaphore(1);

        this.opponentGame = opponentGame;
        animations = new ArrayList<>();

        this.drawBoard = board;
        this.setPreferredSize(new Dimension(800, 750));
        this.setLayout(new BorderLayout());

        displayPanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                BoardAnimation.removeExpiredAnimations(animations);
                updateAnimationWaiter();
                BoardDrawer.drawGraphics(drawBoard, displayPanel, g, opponentGame, animations);
            }
        };
        displayPanel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int[] pos = drawBoard.convertMousePosition(displayPanel, e.getX(), e.getY());
                boardClicked(pos[0], pos[1]);
            }
            @Override
            public void mouseEntered(MouseEvent e) {
            }
            @Override
            public void mouseExited(MouseEvent e) {
            }
            @Override
            public void mousePressed(MouseEvent e) {
            }
            @Override
            public void mouseReleased(MouseEvent e) {
            }
        });

        this.add(displayPanel);

        String buttonText = (opponentGame) ? "<html><center>See<br>Own Ships</center></html>" : "<html><center>See<br>Opponent Board</center></html>";
        JButton switchButton = makeButton(buttonText, Color.ORANGE);
        switchButton.addActionListener((ActionEvent e) -> {
            if (animations.isEmpty()) {
                switchScreens();
            }
        });

        JButton quitButton = makeButton("Quit", Color.RED);
        quitButton.addActionListener((ActionEvent e) -> {
            //TODO: add online shutdown here
            owner.goToMainMenu();
        });

        JPanel buttonBar = new JPanel();
        buttonBar.setPreferredSize(new Dimension(200, 0));
        buttonBar.add(switchButton);
        buttonBar.add(quitButton);

        this.add(buttonBar, BorderLayout.WEST);

        graphicsThread.start();
    }

    public final JButton makeButton(String text, Color bgColor) {
            Font buttonFont = new Font(Font.MONOSPACED, Font.BOLD, 16);

            JButton newButton = new JButton(text);
            newButton.setFocusable(false);
            newButton.setPreferredSize(new Dimension(150, 75));
            newButton.setFont(buttonFont);
            newButton.setBackground(bgColor);
            newButton.setForeground(bgColor.darker().darker());
            newButton.setOpaque(true);
            newButton.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

            return newButton;
        }

    public void addAnimation(BoardAnimation animation) {
        animations.add(animation);
    }

    public void updateAnimationWaiter() {
        if (animations.isEmpty()) {
            if (animationWaiter.availablePermits() == 0) {
                animationWaiter.release();
            }
        } else {
            animationWaiter.tryAcquire();
        }
    }

    public void waitForAnimations() {
        updateAnimationWaiter();
        animationWaiter.acquireUninterruptibly();
        animationWaiter.release();
    }

    public void boardClicked(int x, int y) {
        if (opponentGame) {
            shotProcess(x, y);
        } else {
            //idk
        }
    }

    public void shotProcess(int x, int y) {
        if (!animations.isEmpty()) {
            return;
        }
        if (drawBoard.hasShotPositionAlreadyBeenTried(x, y)) {
            return;
        }
        boolean hit = drawBoard.getShipOnPoint(x, y) != null;
        Runnable shotAnimator = new Runnable() {
            @Override
            public void run() {
                BoardAnimation to = new BoardAnimation(15, x, y, AnimationType.PlaneFlyToPos, true);
                BoardAnimation from = new BoardAnimation(15, x, y, AnimationType.PlaneFlyFromPos, true);
                BoardAnimation explode = new BoardAnimation(11, x, y, AnimationType.BombExplosion, true);
                from.matchRandom(to);
                addAnimation(to);
                waitForAnimations();
                drawBoard.bomb(x, y);
                addAnimation(explode);
                addAnimation(from);
            }
        };
        Thread shotThread = new Thread(shotAnimator);
        shotThread.start();
    }

    public int getTileSize() {
        int panelWidth = this.getWidth(), panelHeight = this.getHeight();
        int tileSize = Math.min(panelWidth / drawBoard.getWidth(), panelHeight / drawBoard.getHeight());
        return tileSize;
    }

    public void switchScreens() {
        if (opponentGame) {
            owner.goToScreen("playerGame");
        } else {
            owner.goToScreen("opponentGame");
        }
    }
}
