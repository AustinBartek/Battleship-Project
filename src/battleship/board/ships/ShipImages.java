package battleship.board.ships;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ShipImages {
    private static final BufferedImage[] fourSides, threeSides, straights, corners, edges, singles, indicators;

    static {
        int numOfStyles = 3;

        fourSides = new BufferedImage[numOfStyles * 4];
        threeSides = new BufferedImage[numOfStyles * 4];
        straights = new BufferedImage[numOfStyles * 4];
        corners = new BufferedImage[numOfStyles * 4];
        edges = new BufferedImage[numOfStyles * 4];
        singles = new BufferedImage[numOfStyles * 4];
        indicators = new BufferedImage[1];

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

            indicators[0] = getResource("miss-indicator");

            count += 4;
        }
    }

    public static BufferedImage getImage(boolean[] connections, int style) {
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

    public static BufferedImage getMissIndicator() {
        return indicators[0];
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

    private static BufferedImage getResource(String name) {
        try {
            return ImageIO.read(ShipImages.class.getClassLoader().getResource("resources/" + name + ".png"));
        } catch (IOException e) {
            System.out.println("null image");
            return null;
        }
    }
}