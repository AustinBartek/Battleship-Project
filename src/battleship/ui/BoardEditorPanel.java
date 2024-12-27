package battleship.ui;

import battleship.Images;
import battleship.board.Board;
import battleship.board.graphics.BoardDrawer;
import battleship.board.ships.Ship;
import battleship.board.ships.Ship.ShipType;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;

public class BoardEditorPanel extends JPanel {
    private Board editingBoard;
    private Ship selectedShip;
    private final JPanel displayPanel;
    private final BattleshipWindow window;
    private final ShipEditorBar shipBar;
    private final ControlPanel buttonBar;

    public BoardEditorPanel(BattleshipWindow owner) {
        this.window = owner;

        setPreferredSize(new Dimension(800, 750));
        setLayout(new BorderLayout());

        displayPanel = new JPanel() {
            @Override
            public void paintComponent(Graphics g) {
                BoardDrawer.drawGraphicsWithShipSelected(editingBoard, displayPanel, g, selectedShip);
            }
        };
        displayPanel.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int[] location = editingBoard.convertMousePosition(displayPanel, e.getX(), e.getY());
                boardClicked(location[0], location[1]);
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });

        this.add(displayPanel);

        shipBar = new ShipEditorBar();
        this.add(shipBar, BorderLayout.WEST);

        buttonBar = new ControlPanel();
        this.add(buttonBar, BorderLayout.SOUTH);

        // Setting up the updating properties of the panel
        final Runnable cursorUpdater = () -> {
            while (true) {
                Point pos = displayPanel.getMousePosition();
                if (pos != null) {
                    int[] location = editingBoard.convertMousePosition(displayPanel, (int) pos.getX(), (int) pos.getY());

                    updateSelectedShipPos(location[0], location[1]);
                }

                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                }
            }
        };
        final Runnable graphicsUpdater = () -> {
            while (true) {
                displayPanel.repaint();
                buttonBar.startButton.repaint();
                try {
                    Thread.sleep(20);
                } catch (InterruptedException e) {
                }
            }
        };
        final Thread cursorThread = new Thread(cursorUpdater);
        final Thread graphicsThread = new Thread(graphicsUpdater);

        final String LEFT = "left";
        final String RIGHT = "right";
        final AbstractAction left = new AbstractAction(LEFT) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedShip != null) {
                    selectedShip.rotateLeft();
                }
            }
        }, right = new AbstractAction(RIGHT) {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (selectedShip != null) {
                    selectedShip.rotateRight();
                }
            }
        };

        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), LEFT);
        this.getActionMap().put(LEFT, left);
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), RIGHT);
        this.getActionMap().put(RIGHT, right);

        cursorThread.start();
        graphicsThread.start();

        resetBoard(9, 9);
    }

    public final void resetBoard(int width, int height) {
        editingBoard = new Board(width, height);
        resetShips();
    }

    public final void resetShips() {
        editingBoard.clearShips();
        selectedShip = null;
        shipBar.setupStandard();
    }

    public final void randomizeShips() {
        for (int i = 0; i < 5; i++) {
            
            ArrayList<Ship> shipsLeft = new ArrayList<>(shipBar.ships);
            shipBar.ships.clear();
            ArrayList<Ship> shipsAdded = new ArrayList<>();

            int failCount = 0;
            while (!shipsLeft.isEmpty() && failCount < 100) {
                Ship currentShip = shipsLeft.remove(0);

                currentShip.setRotation((int) Math.floor(Math.random() * 4));

                int attemptNum = 0, x, y;
                boolean canPlace = false;
                do {
                    x = (int) Math.floor(Math.random() * editingBoard.getWidth());
                    y = (int) Math.floor(Math.random() * editingBoard.getHeight());
                    currentShip.setPosition(x, y);
                    canPlace = editingBoard.canPlaceShip(currentShip);
                    attemptNum++;
                } while (!canPlace && attemptNum < 100);

                if (canPlace) {
                    editingBoard.addShip(currentShip);
                    shipsAdded.add(currentShip);
                } else {
                    failCount++;
                    shipsLeft.add(currentShip);
                }
            }

            if (!shipsLeft.isEmpty()) {
                shipsAdded.addAll(shipsLeft);
                for (Ship addedShip : shipsAdded) {
                    addedShip.setRotation(0);
                    addedShip.setPosition(-1, -1);
                    editingBoard.removeShip(addedShip);
                }
                shipBar.ships.addAll(shipsAdded);
            } else {
                break;
            }

        }

        shipBar.updateButtonPanel();
    }

    public int getTileSize() {
        int panelWidth = this.getWidth(), panelHeight = this.getHeight();
        int tileSize = Math.min(panelWidth / editingBoard.getWidth(), panelHeight / editingBoard.getHeight());
        return tileSize;
    }

    public final void updateSelectedShipPos(int x, int y) {
        if (selectedShip != null) {
            int minX = 0, maxX = editingBoard.getWidth() - 1, minY = 0, maxY = editingBoard.getHeight() - 1;
            if (x >= minX && x <= maxX && y >= minY && y <= maxY) {
                selectedShip.setPosition(x, y);
            }
        }
    }

    public final void boardClicked(int x, int y) {
        if (selectedShip != null) {
            if (editingBoard.canPlaceShip(selectedShip)) {
                editingBoard.addShip(selectedShip);
                selectedShip = null;
            } else {
                shipBar.addShip(selectedShip);
                selectedShip.setPosition(-1, -1);
                selectedShip.setRotation(0);
                selectedShip = null;
            }
        } else {
            Ship shipToUse = editingBoard.getShipOnPoint(x, y);
            if (shipToUse instanceof Ship) {
                editingBoard.removeShip(shipToUse);
                selectedShip = shipToUse;
            }
        }
    }

    private class ShipEditorBar extends JPanel {
        private final ArrayList<Ship> ships;
        private final JScrollPane buttonScroll;
        private final JPanel buttonPanel;

        public ShipEditorBar() {
            setLayout(new BorderLayout());

            ships = new ArrayList<>();

            buttonPanel = new JPanel();
            buttonPanel.setBackground(new Color(78,51,255));
            buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 10));
            buttonPanel.addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (selectedShip != null) {
                        addShip(selectedShip);
                        selectedShip.setPosition(-1, -1);
                        selectedShip.setRotation(0);
                        selectedShip = null;
                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                }

                @Override
                public void mouseExited(MouseEvent e) {
                }
            });

            buttonScroll = new JScrollPane(buttonPanel);
            buttonScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
            buttonScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
            buttonScroll.addMouseWheelListener((MouseWheelEvent e) -> {
                buttonScroll.getVerticalScrollBar()
                        .setValue(buttonScroll.getVerticalScrollBar().getValue() + e.getUnitsToScroll() * 10);
            });
            this.add(buttonScroll);

            updateButtonPanel();
        }

        public final void updateButtonPanel() {
            for (Component comp : buttonPanel.getComponents()) {
                if (comp instanceof ShipButton) {
                    buttonPanel.remove(comp);
                }
            }
            for (Ship ship : this.ships) {
                buttonPanel.add(new ShipButton(ship, this));
            }
            buttonPanel.setPreferredSize(new Dimension(200, this.ships.size() * 170));
            buttonScroll.revalidate();
            buttonScroll.repaint();
        }

        public void setupStandard() {
            ships.clear();
            int[] lengths = { 5, 4, 3, 3, 2 };
            for (int length : lengths) {
                ships.add(new Ship(ShipType.STANDARD, length, 1));
            }
            ships.add(new Ship(ShipType.LSHAPE, 3, 3));
            ships.add(new Ship(ShipType.RECTANGULAR, 3, 3));
            updateButtonPanel();
        }

        public void addShip(Ship ship) {
            if (!ships.contains(ship)) {
                ships.add(ship);
            }
            updateButtonPanel();
        }

        public void removeShip(Ship ship) {
            ships.remove(ship);
            updateButtonPanel();
        }

        public boolean isBarEmpty() {
            return ships.isEmpty();
        }

        private class ShipButton extends JButton {
            private final ShipEditorBar bar;
            private final Ship ship;

            public ShipButton(Ship ship, ShipEditorBar b) {
                this.bar = b;
                this.ship = ship;

                addActionListener((ActionEvent e) -> {
                    if (selectedShip != null) {
                        selectedShip.setPosition(-1, -1);
                        selectedShip.setRotation(0);
                        bar.addShip(selectedShip);
                    }
                    
                    selectedShip = ship;
                    bar.removeShip(ship);
                });

                setPreferredSize(new Dimension(160, 160));
                setFocusable(false);
            }

            @Override
            public void paintComponent(Graphics g) {
                int width = getWidth(), height = getHeight();

                g.setColor(Color.blue);
                g.fillRect(0, 0, width, height);

                BufferedImage image = Images.getFullShipImage(ship);
                int shipLength = image.getHeight() / 32, gridSizeX = width / shipLength,
                        gridSizeY = height / shipLength;

                g.setColor(Color.gray);
                for (int x = 0; x < shipLength; x++) {
                    for (int y = 0; y < shipLength; y++) {
                        g.drawRect(x * gridSizeX, y * gridSizeY, gridSizeX, gridSizeY);
                    }
                }

                double heightRatio = (double) height / image.getHeight();
                int newWidth = (int) (image.getWidth() * heightRatio), xOffset = (width - newWidth) / 2;
                g.drawImage(image, xOffset, 0, newWidth, height, null);
            }
        }
    }

    private class ControlPanel extends JPanel {
        private final JButton startButton;

        public ControlPanel() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));
            setBackground(Color.darkGray);

            startButton = new JButton() {
                @Override
                public void paintComponent(Graphics g) {
                    int buttonTimer = (int) ((System.currentTimeMillis() / 1000.0 * 10) % 19);
                    boolean active = false;
                    Point p = startButton.getMousePosition();
                    if (p instanceof Point) {
                        if (startButton.contains(p)) {
                            active = true;
                        }
                    }
                    if (!active) {
                        buttonTimer %= 6;
                    }
                    BufferedImage buttonImage = Images.getBattleButtonTexture(buttonTimer, active);
                    g.drawImage(buttonImage, 0, 0, startButton.getWidth(), startButton.getHeight(), null);
                }
            };
            startButton.setPreferredSize(new Dimension(200, 100));
            startButton.addActionListener((ActionEvent e) -> {
                if (!shipBar.isBarEmpty() || selectedShip != null) {
                    JOptionPane.showMessageDialog(null, "You must place all of your ships before starting a battle!");
                    return;
                }

                window.addGameScreen(editingBoard.copy());
                //TODO: remove this!! this is for testing
                window.addOpponentGameScreen(editingBoard.copy());

                window.goToScreen("playerGame");
            });
            JButton resetButton = makeButton("RESET", 150, new Color(228, 7, 0));
            resetButton.addActionListener((ActionEvent e) -> {
                resetShips();
            });
            JButton randomButton = makeButton("RANDOM", 150, new Color(255, 0, 211));
            randomButton.addActionListener((ActionEvent e) -> {
                randomizeShips();
            });
            SizeSelector selector = new SizeSelector();

            add(resetButton);
            add(randomButton);
            add(startButton);
            add(selector);
        }

        public final JButton makeButton(String text, int width, Color bgColor) {
            Font buttonFont = new Font(Font.MONOSPACED, Font.BOLD, 30);

            JButton newButton = new JButton(text);
            newButton.setFocusable(false);
            newButton.setPreferredSize(new Dimension(width, 100));
            newButton.setFont(buttonFont);
            newButton.setBackground(bgColor);
            newButton.setForeground(bgColor.darker().darker());
            newButton.setOpaque(true);
            newButton.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));

            return newButton;
        }

        private class SizeSelector extends JPanel {
            private int oldSize = 9;
            
            public SizeSelector() {
                setLayout(new BorderLayout());

                Font sliderFont = new Font(Font.MONOSPACED, Font.BOLD, 12);
    
                JSlider sizeSlider = new JSlider(JSlider.HORIZONTAL);
                sizeSlider.setMinimum(5);
                sizeSlider.setMaximum(12);
                sizeSlider.setMajorTickSpacing(1);
                sizeSlider.setSnapToTicks(true);
                sizeSlider.setPaintLabels(true);
                sizeSlider.setPaintTicks(true);
                sizeSlider.setValue(9);
                sizeSlider.addMouseListener(new MouseListener() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                    }
                    @Override
                    public void mousePressed(MouseEvent e) {
                    }
                    @Override
                    public void mouseReleased(MouseEvent e) {
                        int newSize = sizeSlider.getValue();
                        if (newSize != oldSize) {
                            oldSize = newSize;

                            resetBoard(newSize, newSize);
                        }
                    }
                    @Override
                    public void mouseEntered(MouseEvent e) {
                    }
                    @Override
                    public void mouseExited(MouseEvent e) {
                    }
                });
                sizeSlider.setBackground(Color.lightGray);
                sizeSlider.setForeground(new Color(200, 0, 0));
                sizeSlider.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
                sizeSlider.setFont(sliderFont);
                sizeSlider.setFocusable(false);
                sizeSlider.setPreferredSize(new Dimension(150, 100));
                
                this.add(sizeSlider);
            }
        }
    }
}
