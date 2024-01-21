public class Maze {

    private int width;
    private int height;
    private int[][] weights;
    private boolean[][] walls;

    public Maze(int width, int height) {
        this.width = width;
        this.height = height;
        this.weights = new int[width][height];
        this.walls = new boolean[width][height];

        // initializing weights and walls
        for(int x=0; x<width; x++) {
            for(int y=0; y<height; y++) {
                weights[x][y] = 1;
                walls[x][y] = Math.random() < 0.3;
            }
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isWallBetween(Juncture j1, Juncture j2) {
        if (!areAdjacent(j1, j2)) {
            throw new IllegalArgumentException("Junctures not adjacent");
        }

        int x1 = j1.getX();
        int y1 = j1.getY();
        int x2 = j2.getX();
        int y2 = j2.getY();

        // checking for vertical wall
        if (x1 == x2) {
            int minY = Math.min(y1, y2);
            return walls[x1][minY]; // wall on lower side of upper juncture
        }
        // checking for horizontal wall
        else {
            int minX = Math.min(x1, x2);
            return walls[minX][y1]; // wall on right side of left juncture
        }
    }

    public int getWeightBetween(Juncture j1, Juncture j2) {
        if(!areAdjacent(j1, j2)) {
            throw new IllegalArgumentException("Junctures not adjacent");
        }

        int x1 = j1.getX();
        int y1 = j1.getY();

        return weights[x1][y1];
    }

    private boolean areAdjacent(Juncture j1, Juncture j2) {
        int x1 = j1.getX();
        int y1 = j1.getY();
        int x2 = j2.getX();
        int y2 = j2.getY();

        // junctures are adjacent if they differ by 1 in either x or y, but not both
        return (Math.abs(x1 - x2) == 1 && y1 == y2) || (Math.abs(y1 - y2) == 1 && x1 == x2);
    }

    public boolean isWallAt(Juncture juncture) {
        int x = juncture.getX();
        int y = juncture.getY();
        return x >= 0 && x < width && y >= 0 && y < height && walls[x][y];
    }


}
