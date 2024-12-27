package battleship.board.ships;

public class Ship {
    private int xPos, yPos;
    private int[][] footprint;
    private final int length1, length2;
    private final boolean[] hits;
    private final ShipType type;
    private int rotation;
    private int decorationType;

    public enum ShipType {
        STANDARD,
        LSHAPE,
        RECTANGULAR;
    }

    public Ship(ShipType type, int length1, int length2) {
        this.length1 = length1;
        this.length2 = length2;
        this.type = type;
        this.rotation = 0;
        this.decorationType = (int) Math.floor(Math.random()*3);

        setPosition(-1, -1);
        fixFootprint();

        this.hits = new boolean[footprint.length];
    }

    public void setX(int xPos) {
        this.xPos = xPos;
        fixFootprint();
    }

    public void setY(int yPos) {
        this.yPos = yPos;
        fixFootprint();
    }

    public final void setPosition(int xPos, int yPos) {
        this.xPos = xPos;
        this.yPos = yPos;
        fixFootprint();
    }

    public final void hit(int num) {
        this.hits[num] = true;
    }

    public final void rotateLeft() {
        this.rotation--;
        if (this.rotation < 0) {
            this.rotation = 3;
        }
        fixFootprint();
    }

    public final void rotateRight() {
        this.rotation = (this.rotation + 1) % 4;
        fixFootprint();
    }
    
    public final void setRotation(int r) {
        this.rotation = r % 4;
        fixFootprint();
    }

    public final void fixFootprint() {
        this.footprint = generateFootprint();
    }

    public int getX() {
        return xPos;
    }

    public int getY() {
        return yPos;
    }

    public int getRotation() {
        return rotation;
    }

    public int getStyle() {
        return this.decorationType;
    }

    public int[] getDimensions() {
        return new int[] {length1, length2};
    }

    public boolean[] getHits() {
        return this.hits;
    }

    public final int[][] getFootprint() {
        return this.footprint;
    }

    public final ShipType getType() {
        return this.type;
    }

    public boolean isSunk() {
        boolean sunk = true;
        for (boolean hit : this.hits) {
            sunk &= hit;
        }
        return sunk;
    }

    public int[][] generateFootprint() {
        switch (this.type) {
            case LSHAPE:
                return FootprintGenerator.generateLShapedFootprint(xPos, yPos, length1, length2, rotation);
            case RECTANGULAR:
                return FootprintGenerator.generateRectangularFootprint(xPos, yPos, length1, length2, rotation);
            case STANDARD:
                return FootprintGenerator.generateStandardFootprint(xPos, yPos, length1, length2, rotation);
            default:
                return FootprintGenerator.generateStandardFootprint(xPos, yPos, length1, length2, rotation);
        }
    }

    public boolean footprintContainsPoint(int testX, int testY) {
        for (int[] pos : footprint) {
            if (testX == pos[0] && testY == pos[1]) {
                return true;
            }
        }
        return false;
    }

    public boolean[][] getFootprintConnections() { //connection order is top-right-down-left
        boolean[][] connections = new boolean[footprint.length][4];
        int count = 0;
        for (int[] pos : footprint) {
            int currentX = pos[0], currentY = pos[1];
            connections[count] = new boolean[] {footprintContainsPoint(currentX, currentY - 1), footprintContainsPoint(currentX + 1, currentY), footprintContainsPoint(currentX, currentY + 1), footprintContainsPoint(currentX - 1, currentY)};
            count++;
        }

        return connections;
    }

    public Ship copy() {
        Ship copy = new Ship(this.type, this.length1, this.length2);
        for (int i = 0; i < hits.length; i++) {
            copy.hits[i] = hits[i];
        }
        copy.decorationType = decorationType;
        copy.rotation = rotation;
        copy.xPos = xPos;
        copy.yPos = yPos;
        copy.fixFootprint();
        return copy;
    }
}