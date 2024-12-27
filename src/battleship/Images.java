package battleship;

import battleship.board.ships.Ship;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Transparency;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Images {
    private static final BufferedImage[] fourSides, threeSides, straights, corners, edges, singles, indicators,
            waterAnimation, waterDarkAnimation, fireAnimation, battleButtonInactive, battleButtonActive, bombExplosion, bombDropping, sunkenShip;
    private static final BufferedImage[][] allBaseImages;
    private static final BufferedImage hitCursor, bomberPlane;

    static {
        int numOfStyles = 3;

        fourSides = new BufferedImage[numOfStyles * 4];
        threeSides = new BufferedImage[numOfStyles * 4];
        straights = new BufferedImage[numOfStyles * 4];
        corners = new BufferedImage[numOfStyles * 4];
        edges = new BufferedImage[numOfStyles * 4];
        singles = new BufferedImage[numOfStyles * 4];
        indicators = new BufferedImage[1];
        waterAnimation = new BufferedImage[16];
        waterDarkAnimation = new BufferedImage[16];
        fireAnimation = new BufferedImage[32];
        battleButtonInactive = new BufferedImage[6];
        battleButtonActive = new BufferedImage[19];
        bombExplosion = new BufferedImage[11];
        bombDropping = new BufferedImage[9];
        sunkenShip = new BufferedImage[4];

        int count = 0;
        for (int i = 1; i <= numOfStyles; i++) {
            BufferedImage four = getResource("ship-allSides-" + i);
            BufferedImage three = getResource("ship-3Sides-" + i);
            BufferedImage straight = getResource("ship-straight-" + i);
            BufferedImage corner = getResource("ship-corner-" + i);
            BufferedImage edge = getResource("ship-edge-" + i);
            BufferedImage single = getResource("ship-single-" + i);

            for (int j = 0; j < 4; j++) {
                int angle = j * 90, index = count + j;

                fourSides[index] = rotateImageByDegrees(four, angle);
                threeSides[index] = rotateImageByDegrees(three, angle);
                straights[index] = rotateImageByDegrees(straight, angle);
                corners[index] = rotateImageByDegrees(corner, angle);
                edges[index] = rotateImageByDegrees(edge, angle);
                singles[index] = rotateImageByDegrees(single, angle);
            }

            count += 4;
        }

        indicators[0] = getResource("miss-indicator");

        BufferedImage waterSheet = getResource("water-animation");
        for (int i = 0; i < 16; i++) {
            waterAnimation[i] = waterSheet.getSubimage(i * 32, 0, 32, 32);
        }

        BufferedImage waterDarkSheet = getResource("enemy-water-animation");
        for (int i = 0; i < 16; i++) {
            waterDarkAnimation[i] = waterDarkSheet.getSubimage(i * 32, 0, 32, 32);
        }

        BufferedImage fireSheet = getResource("fire-animation");
        for (int i = 0; i < 32; i++) {
            fireAnimation[i] = fireSheet.getSubimage(0, i * 16, 16, 16);
        }

        BufferedImage bbInactiveSheet = getResource("battle-button");
        for (int i = 0; i < 6; i++) {
            battleButtonInactive[i] = bbInactiveSheet.getSubimage(i*64, 0, 64, 32);
        }

        BufferedImage bbActiveSheet = getResource("battle-button-fire");
        for (int i = 0; i < 19; i++) {
            battleButtonActive[i] = bbActiveSheet.getSubimage(i*64, 0, 64, 32);
        }

        BufferedImage bombExplosionSheet = getResource("bomb-explosion");
        for (int i = 0; i < 11; i++) {
            bombExplosion[i] = bombExplosionSheet.getSubimage(i*32, 0, 32, 32);
        }

        BufferedImage bombDroppingSheet = getResource("bomb-dropping-animation");
        for (int i = 0; i < 9; i++) {
            bombDropping[i] = bombDroppingSheet.getSubimage(i*32, 0, 32, 32);
        }

        BufferedImage sunkenSheet = getResource("sunken-ship-animation");
        for (int i = 0; i < 4; i++) {
            sunkenShip[i] = sunkenSheet.getSubimage(i*32, 0, 32, 32);
        }

        hitCursor = getResource("hit-cursor");
        bomberPlane = getResource("bomber-plane");

        allBaseImages = new BufferedImage[][] { fourSides, threeSides, straights,
                corners, edges, singles, indicators, waterAnimation, waterDarkAnimation, fireAnimation, battleButtonActive, battleButtonInactive, bombExplosion, bombDropping, sunkenShip, {hitCursor, bomberPlane} };
        
        for (BufferedImage[] list : allBaseImages) {
            for (int i = 0; i < list.length; i++) {
                list[i] = convertToUseableFormat(list[i]);
            }
        }
    }

    public static BufferedImage getShipImage(boolean[] connections, int style) {
        if (connections.length != 4) {
            System.out.println("incorrect connection length");
            return null;
        }

        int styleNum = style * 4;
        boolean up = connections[0], right = connections[1], down = connections[2], left = connections[3],
                consecutive = false;

        boolean previous = connections[0];
        for (int i = 1; i < connections.length; i++) {
            boolean test = connections[i];
            consecutive |= (test && previous);
            previous = test;
        }
        consecutive |= (up && left);

        int numConnections = ((up) ? 1 : 0) + ((right) ? 1 : 0) + ((down) ? 1 : 0) + ((left) ? 1 : 0);

        switch (numConnections) {
            case 4:
                return fourSides[styleNum];
            case 3:
                int rotation;
                if (!up) {
                    rotation = 0;
                } else if (!right) {
                    rotation = 1;
                } else if (!down) {
                    rotation = 2;
                } else {
                    rotation = 3;
                }

                return threeSides[styleNum + rotation];
            case 2:
                if (consecutive) { // corner piece
                    if (up) {
                        if (right) {
                            rotation = 3;
                        } else {
                            rotation = 2;
                        }
                    } else {
                        if (right) {
                            rotation = 0;
                        } else {
                            rotation = 1;
                        }
                    }
                    return corners[styleNum + rotation];
                } else { // straight piece
                    if (up) {
                        rotation = 0;
                    } else {
                        rotation = 1;
                    }
                    return straights[styleNum + rotation];
                }
            case 1:
                if (up) {
                    rotation = 2;
                } else if (right) {
                    rotation = 3;
                } else if (down) {
                    rotation = 0;
                } else {
                    rotation = 1;
                }
                return edges[styleNum + rotation];
            case 0:
                return singles[styleNum];
        }

        return null;
    }

    public static BufferedImage getFullShipImage(Ship ship) {
        int[][] footprint = ship.getFootprint();
        boolean[][] allConnections = ship.getFootprintConnections();
        int maxX = Integer.MIN_VALUE, maxY = Integer.MIN_VALUE, shipX = ship.getX(), shipY = ship.getY();

        for (int[] position : footprint) {
            int x = position[0], y = position[1];
            int diffX = x - shipX, diffY = y - shipY;
            maxX = Math.max(diffX, maxX);
            maxY = Math.max(diffY, maxY);
        }

        BufferedImage[][] allImages = new BufferedImage[maxX+1][maxY+1];
        for (int i = 0; i < footprint.length; i++) {
            int[] position = footprint[i];
            int x = position[0], y = position[1];
            int diffX = x-shipX, diffY = y-shipY;
            boolean[] connections = allConnections[i];
            BufferedImage newImage = getShipImage(connections, ship.getStyle());
            allImages[diffX][diffY] = newImage;
        }

        BufferedImage finalImage = new BufferedImage(allImages.length*32, allImages[0].length*32, allImages[0][0].getType());
        Graphics g = finalImage.getGraphics();
        for (int x = 0; x < allImages.length; x++) {
            for (int y = 0; y < allImages[0].length; y++) {
                g.drawImage(allImages[x][y], x*32, y*32, null);
            }
        }
        return finalImage;
    }

    public static BufferedImage getMissIndicator() {
        return indicators[0];
    }

    public static BufferedImage getWaterTexture(int animation, boolean dark) {
        if (dark) {
            return waterDarkAnimation[animation];
        }
        return waterAnimation[animation];
    }

    public static BufferedImage getFireTexture(int animation) {
        return fireAnimation[animation];
    }

    public static BufferedImage getBattleButtonTexture(int animation, boolean active) {
        if (active) {
            return battleButtonActive[animation];
        }

        return battleButtonInactive[animation];
    }

    public static BufferedImage getHitCursor() {
        return hitCursor;
    }

    public static BufferedImage getBomberPlane() {
        return bomberPlane;
    }

    public static BufferedImage getBombExplosion(int animation) {
        return bombExplosion[animation];
    }

    public static BufferedImage getSunkenTexture(int animation) {
        return sunkenShip[animation];
    }

    private static BufferedImage rotateImageByDegrees(BufferedImage img, double angle) {
        double rads = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
        int w = img.getWidth();
        int h = img.getHeight();
        int newWidth = (int) Math.floor(w * cos + h * sin);
        int newHeight = (int) Math.floor(h * cos + w * sin);

        BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = rotated.createGraphics();
        AffineTransform at = new AffineTransform();
        at.translate((newWidth - w) / 2, (newHeight - h) / 2);

        int x = w / 2;
        int y = h / 2;

        at.rotate(rads, x, y);
        g2d.setTransform(at);
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();

        return rotated;
    }

    public static BufferedImage convertToUseableFormat(BufferedImage img) {
        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice device = env.getDefaultScreenDevice();
        GraphicsConfiguration config = device.getDefaultConfiguration();

        int width = img.getWidth(), height = img.getHeight();
        BufferedImage buffy = config.createCompatibleImage(width, height, Transparency.TRANSLUCENT);
        Graphics g = buffy.getGraphics();
        g.drawImage(img, 0, 0, width, height, null);
        return buffy;
    }

    public static BufferedImage resizeImage(BufferedImage image, int width, int height) {
        BufferedImage resized = new BufferedImage(width, height, image.getType());
        Graphics2D g = resized.createGraphics();
        g.drawImage(image, 0, 0, width, height, null);
        g.dispose();
        return resized;
    }

    public static BufferedImage[] copyList(BufferedImage[] list) {
        BufferedImage[] newList = new BufferedImage[list.length];
        for (int i = 0; i < newList.length; i++) {
            newList[i] = copyImage(list[i]);
        }
        return newList;
    }

    public static BufferedImage copyImage(BufferedImage image) {
        BufferedImage copy = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        Graphics2D g = copy.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return copy;
    }

    private static BufferedImage getResource(String name) {
        try {
            return ImageIO.read(Images.class.getClassLoader().getResource("resources/" + name + ".png"));
        } catch (IOException e) {
            System.out.println("null image");
            return null;
        }
    }
}