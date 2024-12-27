package battleship.board.ships;

public class HitHelper {
    public static int getIndexOfHit(int xPos, int yPos, int[][] footprint) {
        for (int i = 0; i < footprint.length; i++) {
            int[] position = footprint[i];
            if (position[0] == xPos && position[1] == yPos) {
                return i;
            }
        }
        
        return -1;
    }
}
