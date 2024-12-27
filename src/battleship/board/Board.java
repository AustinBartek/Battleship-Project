package battleship.board;

import battleship.board.ships.HitHelper;
import battleship.board.ships.Ship;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.swing.JPanel;

public class Board {
    private final HashMap<String, Boolean> rules;
    private final int width, height;
    private final ArrayList<Ship> ships;
    private final ArrayList<int[]> shotAttempts;

    public Board() {
        this.width = 9;
        this.height = 9;
        this.ships = new ArrayList<>();
        this.shotAttempts = new ArrayList<>();
        this.rules = new HashMap<>();
        initializeRules();
    }

    public Board(int width, int height) {
        this.width = width;
        this.height = height;
        this.ships = new ArrayList<>();
        this.shotAttempts = new ArrayList<>();
        this.rules = new HashMap<>();
        initializeRules();
    }

    public final void initializeRules() {
        this.rules.put("spacing", false);
        this.rules.put("streak", false);
    }

    public final Boolean getRule(String rule) {
        return rules.get(rule);
    }

    public final void setRule(String rule, boolean condition) {
        if (rules.containsKey(rule)) {
            rules.put(rule, condition);
        }
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public ArrayList<Ship> getShips() {
        return new ArrayList<>(this.ships);
    }

    public ArrayList<int[]> getShotAttempts() {
        return new ArrayList<>(this.shotAttempts);
    }

    public boolean canPlaceShip(Ship ship) {
        int[][] footprint = ship.getFootprint();
        for (int[] pos : footprint) {
            if (!isSquarePlaceable(pos[0], pos[1])) {
                return false;
            }
        }
        return true;
    }

    public boolean isSquarePlaceable(int xPos, int yPos) {
        boolean check = getShipOnPoint(xPos, yPos) == null && isValidPosition(xPos, yPos);
        if (getRule("spacing")) {
            check &= !isNearbyShips(xPos, yPos);
        }
        return check;
    }

    public boolean isNearbyShips(int xPos, int yPos) {
        boolean spacingCheck = false;
        for (int x = xPos - 1; x <= xPos + 1; x++) {
            for (int y = yPos - 1; y <= yPos + 1; y++) {
                spacingCheck |= getShipOnPoint(x, y) != null;
            }
        }
        return spacingCheck;
    }

    public boolean isValidPosition(int xPos, int yPos) {
        return (xPos >= 0 && xPos < width && yPos >= 0 && yPos < height);
    }

    public Ship getShipOnPoint(int xPos, int yPos) {
        for (Ship ship : ships) {
            if (ship.footprintContainsPoint(xPos, yPos)) {
                return ship;
            }
        }
        return null;
    }

    public void bomb(int xPos, int yPos) {
        shotAttempts.add(new int[] { xPos, yPos });
        Ship ship = getShipOnPoint(xPos, yPos);

        if (ship != null) { // HIT
            int hitPos = convertToHitPosition(ship, xPos, yPos);
            ship.hit(hitPos);
        }
    }

    public boolean hasShotPositionAlreadyBeenTried(int xPos, int yPos) {
        for (int[] pos : shotAttempts) {
            if (xPos == pos[0] && yPos == pos[1]) {
                return true;
            }
        }
        return false;
    }

    public int convertToHitPosition(Ship ship, int xPos, int yPos) {
        return HitHelper.getIndexOfHit(xPos, yPos, ship.getFootprint());
    }

    public boolean isDefeated() {
        boolean allSunk = true;
        for (Ship ship : this.ships) {
            allSunk &= ship.isSunk();
        }
        return allSunk;
    }

    public void addShip(Ship ship) {
        this.ships.add(ship);
    }

    public void removeShip(Ship ship) {
        this.ships.remove(ship);
    }

    public void clearShips() {
        this.ships.clear();
    }

    public int[] convertMousePosition(JPanel panel, int x, int y) {
        int panelWidth = panel.getWidth(), panelHeight = panel.getHeight();
        int tileSize = Math.min(panelWidth / width, panelHeight / height);
        int boardWidth = tileSize * width, boardHeight = tileSize * height;
        int xOffset = (panelWidth - boardWidth) / 2, yOffset = (panelHeight - boardHeight) / 2;

        int xPos = (x - xOffset) / tileSize, yPos = (y - yOffset) / tileSize;
        return new int[] { xPos, yPos };
    }

    public Board copy() {
        Board copy = new Board(this.width, this.height);
        for (Ship ship : this.ships) {
            copy.addShip(ship.copy());
        }
        for (Entry<String, Boolean> entry : rules.entrySet()) {
            copy.rules.put(entry.getKey(), entry.getValue());
        }
        for (int[] position : shotAttempts) {
            copy.shotAttempts.add(new int[] {position[0], position[1]});
        }
        return copy;
    }
}
