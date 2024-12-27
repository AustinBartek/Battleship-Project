package battleship.board.graphics;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;
import java.util.function.Predicate;

import battleship.Images;

public class BoardAnimation {
    private final static Random rand = new Random();
    private final int maxFrames, xPos, yPos;
    private final AnimationType type;
    private final boolean concurrent;
    private int randomSeed;
    private int currentFrame;

    public BoardAnimation(int maxFrames, int x, int y, AnimationType type, boolean concurrent) {
        this.maxFrames = maxFrames;
        this.xPos = x;
        this.yPos = y;
        this.type = type;
        this.concurrent = concurrent;
        currentFrame = 0;

        this.randomSeed = (int) (Math.random() * Integer.MAX_VALUE);
    }

    public boolean isExpired() {
        return currentFrame >= maxFrames;
    }

    public enum AnimationType {
        PlaneFlyToPos,
        PlaneFlyFromPos,
        BombExplosion,
        BombDropping;

    }

    public void drawAnimation(Graphics g, int boardTileWidth, int boardTileHeight, int panelWidth, int panelHeight, int xOffset, int yOffset, int tileSize) {
        rand.setSeed(randomSeed);

        switch (type) {
            case PlaneFlyToPos:
                int planeSize = (int) (16.0 / 32 * tileSize);
                double progress = (double) currentFrame/maxFrames;
                int boardWidth = boardTileWidth*tileSize;
                int boardHeight = boardTileHeight*tileSize;
                int useXPos = (int) ((xPos + 0.5) * tileSize + planeSize/2);
                int useYPos = (int) ((yPos + 0.5) * tileSize + planeSize/2);
                int leftYPos = rand.nextInt(boardHeight/10 + 1) - boardHeight/20 + useYPos;
                double slope = (((double) leftYPos - useYPos)/(-useXPos));
                double boardX = boardWidth * progress;
                int planeX = (int) boardX + xOffset;
                int planeY = (int) (leftYPos + boardX * slope) + yOffset;

                BufferedImage bomberPlane = Images.getBomberPlane();
                g.drawImage(bomberPlane, planeX - planeSize/2, planeY-planeSize/2, planeSize, planeSize, null);

                if ((planeX - xOffset) >= useXPos) {
                    endAnimation();
                }
                break;
            case PlaneFlyFromPos:
                planeSize = (int) (16.0 / 32 * tileSize);
                boardWidth = boardTileWidth*tileSize;
                boardHeight = boardTileHeight*tileSize;
                useXPos = (int) ((xPos + 0.5) * tileSize + planeSize/2);
                useYPos = (int) ((yPos + 0.5) * tileSize + planeSize/2);
                int useFrame = currentFrame + (int) ((double) useXPos/boardWidth * maxFrames) + 1;
                progress = (double) useFrame/maxFrames;
                leftYPos = rand.nextInt(boardHeight/10 + 1) - boardHeight/20 + useYPos;
                slope = (((double) leftYPos - useYPos)/(-useXPos));
                boardX = boardWidth * progress;
                planeX = (int) boardX + xOffset;
                planeY = (int) (leftYPos + boardX * slope) + yOffset;

                bomberPlane = Images.getBomberPlane();
                g.drawImage(bomberPlane, planeX - planeSize/2, planeY-planeSize/2, planeSize, planeSize, null);

                if (useFrame >= maxFrames) {
                    endAnimation();
                }
            case BombExplosion:
                int explosionFrameToUse = (int) ((double) currentFrame/maxFrames * 10);
                BufferedImage explosionImage = Images.getBombExplosion(explosionFrameToUse);
                g.drawImage(explosionImage, xPos*tileSize+xOffset, yPos*tileSize+yOffset, tileSize, tileSize, null);
            case BombDropping:
                
            default:
                break;
        }

        currentFrame++;
    }

    public void endAnimation() {
        currentFrame = maxFrames;
    }

    public void matchRandom(BoardAnimation other) {
        this.randomSeed = other.randomSeed;
    }

    public static ArrayList<BoardAnimation> getCurrentAnimations(ArrayList<BoardAnimation> animations) {
        ArrayList<BoardAnimation> currents = new ArrayList<>();
        for (BoardAnimation anim : animations) {
            currents.add(anim);
            if (!anim.concurrent) {
                break;
            }
        }
        return currents;
    }

    public static void removeExpiredAnimations(ArrayList<BoardAnimation> animations) {
        animations.removeIf(new Predicate<BoardAnimation>() {
            @Override
            public boolean test(BoardAnimation t) {
                return t.isExpired();
            }
        });
    }
}
