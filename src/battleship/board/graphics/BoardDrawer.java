package battleship.board.graphics;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;

import battleship.Images;
import battleship.board.Board;
import battleship.board.ships.Ship;

public class BoardDrawer {
    // TODO: add an animation class that is ticked whenever the board is drawn, and
    // that has a queue so that only one animation (or more if they allow
    // concurrence) goes at a time.

    private static final Color GRIDCOLOR = new Color(0, 75, 150), ENEMYGRIDCOLOR = new Color(100, 100, 100),
            BACKGROUNDCOLOR = new Color(0, 25, 50),
            ALLOWCOLOR = new Color(0, 255, 0), DENYCOLOR = new Color(255, 0, 0);

    public static void drawGraphics(Board board, JPanel panel, Graphics g, boolean opponentBoard,
            ArrayList<BoardAnimation> animations) {
        int width = board.getWidth(), height = board.getHeight();
        int panelWidth = panel.getWidth(), panelHeight = panel.getHeight();
        int tileSize = Math.min(panelWidth / width, panelHeight / height);
        int boardWidth = tileSize * width, boardHeight = tileSize * height;
        int xOffset = (panelWidth - boardWidth) / 2, yOffset = (panelHeight - boardHeight) / 2;
        int waterTime = (int) ((System.currentTimeMillis() / 1000.0 * 15) % 16);
        BufferedImage waterImage = Images.getWaterTexture(waterTime, opponentBoard);
        int fireTime = (int) ((System.currentTimeMillis() / 1000.0 * 20) % 32);
        BufferedImage fireImage = Images.getFireTexture(fireTime);
        int sunkenTime = (int) ((System.currentTimeMillis() / 1000.0 * 5) % 4);
        BufferedImage sunkenImage = Images.getSunkenTexture(sunkenTime);

        g.setColor(BACKGROUNDCOLOR);
        g.fillRect(0, 0, panelWidth, panelHeight);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                g.drawImage(waterImage, x * tileSize + xOffset, y * tileSize + yOffset, tileSize, tileSize, null);
            }
        }

        // draws the grid-like pattern you see on the board
        if (opponentBoard) {
            g.setColor(ENEMYGRIDCOLOR);
        } else {
            g.setColor(GRIDCOLOR);
        }
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int xPos = x * tileSize + xOffset, yPos = y * tileSize + yOffset;
                g.drawRect(xPos, yPos, tileSize, tileSize);
            }
        }

        for (Ship ship : board.getShips()) {
            int[][] footprint = ship.getFootprint();
            boolean[] hits = ship.getHits();

            if (ship.isSunk()) {

                for (int i = 0; i < footprint.length; i++) {
                    int[] position = footprint[i];
                    boolean hit = hits[i];
                    int xPos = position[0] * tileSize + xOffset, yPos = position[1] * tileSize + yOffset;
                    g.drawImage(sunkenImage, xPos, yPos, tileSize, tileSize, null);

                    if (hit) {
                        g.drawImage(fireImage, xPos + tileSize / 4, yPos + tileSize / 4, tileSize / 2, tileSize / 2,
                                null);
                    }
                }

            } else {

                boolean[][] connections = ship.getFootprintConnections();

                for (int i = 0; i < footprint.length; i++) {
                    int[] position = footprint[i];
                    boolean hit = hits[i];
                    boolean[] connection = connections[i];

                    int xPos = position[0] * tileSize + xOffset, yPos = position[1] * tileSize + yOffset;

                    if (!opponentBoard) {
                        BufferedImage partImage = Images.getShipImage(connection, ship.getStyle());
                        g.drawImage(partImage, xPos, yPos, tileSize, tileSize, null);
                    }

                    if (hit) {
                        g.drawImage(fireImage, xPos + tileSize / 4, yPos + tileSize / 4, tileSize / 2, tileSize / 2,
                                null);
                    }
                }

            }
        }

        BufferedImage missIndicator = Images.getMissIndicator();
        for (int[] pos : board.getShotAttempts()) {
            int xPos = pos[0] * tileSize + xOffset, yPos = pos[1] * tileSize + yOffset;
            if (board.getShipOnPoint(pos[0], pos[1]) != null) {
                continue;
            }

            g.drawImage(missIndicator, xPos, yPos, tileSize, tileSize, null);
        }

        ArrayList<BoardAnimation> useAnims = BoardAnimation.getCurrentAnimations(animations);
        for (BoardAnimation anim : useAnims) {
            anim.drawAnimation(g, width, height, panelWidth, panelHeight, xOffset, yOffset, tileSize);
        }
    }

    public static void drawGraphicsWithShipSelected(Board board, JPanel panel, Graphics g, Ship drawShip) {
        int width = board.getWidth(), height = board.getHeight();
        int panelWidth = panel.getWidth(), panelHeight = panel.getHeight();
        int tileSize = Math.min(panelWidth / width, panelHeight / height);
        int boardWidth = tileSize * width, boardHeight = tileSize * height;
        int xOffset = (panelWidth - boardWidth) / 2, yOffset = (panelHeight - boardHeight) / 2;
        int waterTime = (int) ((System.currentTimeMillis() / 1000.0 * 15) % 16);
        BufferedImage waterImage = Images.getWaterTexture(waterTime, false);

        g.setColor(BACKGROUNDCOLOR);
        g.fillRect(0, 0, panelWidth, panelHeight);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                g.drawImage(waterImage, x * tileSize + xOffset, y * tileSize + yOffset, tileSize, tileSize, null);
            }
        }

        if (drawShip != null) {
            Color allowanceColor;
            if (board.canPlaceShip(drawShip)) {
                allowanceColor = ALLOWCOLOR;
            } else {
                allowanceColor = DENYCOLOR;
            }
            g.setColor(allowanceColor);
            for (int[] pos : drawShip.getFootprint()) {
                g.fillRect(pos[0] * tileSize + xOffset, pos[1] * tileSize + yOffset, tileSize, tileSize);
            }
        }

        // draws the grid-like pattern you see on the board
        g.setColor(GRIDCOLOR);
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int xPos = x * tileSize + xOffset, yPos = y * tileSize + yOffset;
                g.drawRect(xPos, yPos, tileSize, tileSize);
            }
        }

        for (Ship ship : board.getShips()) {
            int[][] footprint = ship.getFootprint();
            boolean[][] connections = ship.getFootprintConnections();

            for (int i = 0; i < footprint.length; i++) {
                int[] position = footprint[i];
                boolean[] connection = connections[i];

                int xPos = position[0] * tileSize + xOffset, yPos = position[1] * tileSize + yOffset;

                BufferedImage partImage = Images.getShipImage(connection, ship.getStyle());
                g.drawImage(partImage, xPos, yPos, tileSize, tileSize, null);
            }
        }
    }
}