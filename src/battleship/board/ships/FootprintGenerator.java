package battleship.board.ships;

public class FootprintGenerator {

    //ENSURE ALL FOOTPRINTS HAVE LEAST X-VALUE AND THEN LEAST Y-VALUE ORDER!! FOR CHECKING HITS

    public static int[][] generateStandardFootprint(int xPos, int yPos, int length1, int length2, int rotation) {
        length1 = Math.max(length1, 1);

        int[][] newFootprint = new int[length1][2];
        int count = 0;
        if (rotation % 2 == 0) {
            for (int testY = yPos; testY < yPos + length1; testY++) {
                newFootprint[count] = new int[] {xPos, testY};
                count++;
            }
        } else {
            for (int testX = xPos; testX < xPos + length1; testX++) {
                newFootprint[count] = new int[] {testX, yPos};
                count++;
            }
        }

        return newFootprint;
    }

    public static int[][] generateLShapedFootprint(int xPos, int yPos, int length1, int length2, int rotation) {
        length1 = Math.max(length1, 1);
        length2 = Math.max(length2, 1);

        int[][] newFootprint = new int[length1 + length2 - 1][2];
        int count = 0;
        int maxX = xPos + length2, maxY = yPos + length1, maxXRotated = xPos + length1, maxYRotated = yPos + length2;
        switch (rotation) {
            case 0: //down + right
                for (int testY = yPos; testY < maxY; testY++) {
                    newFootprint[count] = new int[] {xPos, testY};
                    count++;
                }
                for (int testX = xPos + 1; testX < maxX; testX++) {
                    newFootprint[count] = new int[] {testX, yPos};
                    count++;
                }
            break;
            case 1: //down + left
                for (int testX = xPos; testX < maxXRotated - 1; testX++) {
                    newFootprint[count] = new int[] { testX, yPos };
                    count++;
                }
                for (int testY = yPos; testY < maxYRotated; testY++) {
                    newFootprint[count] = new int[] { maxXRotated - 1, testY };
                    count++;
                }
            break;
            case 2: //up + left
                for (int testX = xPos; testX < maxX - 1; testX++) {
                    newFootprint[count] = new int[] { testX, maxY - 1 };
                    count++;
                }
                for (int testY = yPos; testY < maxY; testY++) {
                    newFootprint[count] = new int[] { maxX - 1, testY };
                    count++;
                }
            break;
            case 3: //up + right
                for (int testY = yPos; testY < maxYRotated; testY++) {
                    newFootprint[count] = new int[] { xPos, testY };
                    count++;
                }
                for (int testX = xPos + 1; testX < maxXRotated; testX++) {
                    newFootprint[count] = new int[] { testX, maxYRotated - 1 };
                    count++;
                }
            break;
            default: //down + right
                System.out.println("invalid rotation in l-shaped footprint generator");
                for (int testY = yPos; testY < yPos + length1; testY++) {
                    newFootprint[count] = new int[] { xPos, testY };
                    count++;
                }
                for (int testX = xPos + 1; testX < xPos + length2; testX++) {
                    newFootprint[count] = new int[] { testX, yPos };
                    count++;
                }
            break;
        }

        return newFootprint;
    }

    public static int[][] generateRectangularFootprint(int xPos, int yPos, int length1, int length2, int rotation) {
        length1 = Math.max(length1, 1);
        length2 = Math.max(length2, 1);

        int footprintSize;
        boolean length1One = (length1 == 1), length2One = (length2 == 1);
        if (length1One && length2One) {
            footprintSize = 1;
        } else if (length1One) {
            footprintSize = length2;
        } else if (length2One) {
            footprintSize = length1;
        } else {
            footprintSize = (length1 + length2) * 2 - 4;
        }

        int[][] newFootprint = new int[footprintSize][2];
        int count = 0;
        int maxX = xPos + length2, maxY = yPos + length1;

        for (int testY = yPos; testY < maxY; testY++) {
            newFootprint[count] = new int[] { xPos, testY };
            count++;
        }
        for (int testX = xPos + 1; testX < maxX - 1; testX++) {
            newFootprint[count] = new int[] { testX, yPos };
            count++;
            if (length1 > 1) {
                newFootprint[count] = new int[] { testX, maxY - 1 };
                count++;
            }
        }
        for (int testY = yPos; testY < maxY; testY++) {
            newFootprint[count] = new int[] { maxX - 1, testY };
            count++;
        }

        return newFootprint;
    }
}
