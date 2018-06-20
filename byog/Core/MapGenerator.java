package byog.Core;
import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class MapGenerator {
    private static final Random RANDOM = new Random();
    private static final int WIDTH = 80;
    private static final int HEIGHT = 40;
    private static final int NROOM = 20;
    private static int ROOMS = 0;

    /** Position is a class with two variables p.x and p.y and no methods.*/
    static class Position {
        int x, y;
        Position(int x, int y){
            this.x = x;
            this.y = y;
        }
    }

    /**
     * Generate hallways
     */
    static void  makeHallway(TETile[][] world, Position p, int width, int height){
        if (p.x + width < WIDTH && p.y + height < HEIGHT ){
            drawFloor(world, new Position(p.x+1, p.y+1), width-2, height-2);
            drawFloor(world, new Position(p.x+1, p.y+1), width-2, height-2);
            drawWall(world, new Position(p.x, p.y), 1,height);
            drawWall(world, new Position(p.x, p.y), width,1);
            drawWall(world, new Position(p.x, p.y+height-1), width,1);
            drawWall(world, new Position(p.x+width-1, p.y), 1,height);
        }

        Position bp = makeBreak(world, p, width, height);

        switch (RANDOM.nextInt(2)) {
            case 0: makeRoom();
            case 1: makeHallway();
            default: makeHallway();
        }


    }
    /** Generate room
     *
     */
    static void makeRoom(TETile[][] world, Position p, int width, int height){
        if (p.x + width < WIDTH && p.y + height < HEIGHT ){
            drawFloor(world, new Position(p.x+1, p.y+1), width-2, height-2);
            drawWall(world, new Position(p.x, p.y), 1,height);
            drawWall(world, new Position(p.x, p.y), width,1);
            drawWall(world, new Position(p.x, p.y+height-1), width,1);
            drawWall(world, new Position(p.x+width-1, p.y), 1,height);
            ROOMS += 1;
        }

        if (ROOMS < NROOM){
            Position bp = makeBreak(world, p, width, height);
            makeHallway();
        }

    }

    /** make break through in current room*/
    static Position makeBreak(TETile[][] world, Position p, int width, int height){
        Position bp;
        switch (RANDOM.nextInt(4)) {
            case 0: bp = new Position(p.x, p.y+RANDOM.nextInt(height));
            case 1: bp = new Position(p.x+width-1, p.y+RANDOM.nextInt(height));
            case 2: bp = new Position(p.x+RANDOM.nextInt(width), p.y);
            case 3: bp = new Position(p.x+RANDOM.nextInt(width), p.y+height-1);
            default: bp = p;
        }
        if (WIDTH - bp.x > 1 && bp.x > 1 && HEIGHT - bp.y > 1 && bp.y > 1) {
            world[bp.x][bp.y] = Tileset.PLAYER;
        }
        return bp;
    }

    /** draw the floor space of a room
     *
     */
    static void drawFloor(TETile[][] world, Position p, int width, int height){
        for (int i=0; i<width; i++){
            for (int j=0; j<height; j++){
                world[i+p.x][j+p.y] = Tileset.FLOOR;
            }
        }
    }

    /** draw the wall
     */
    static void drawWall(TETile[][] world, Position p, int width, int height){
        for (int i=0; i<width; i++){
            for (int j=0; j<height; j++){
                world[i+p.x][j+p.y] = Tileset.WALL;
            }
        }
    }

    /** Returns a pseudo-random number between min and max, inclusive.
     * The difference between min and max can be at most
     * Integer.MAX_VALUE - 1.*/
    public static int randInt(int min, int max) {
        return RANDOM.nextInt((max - min) + 1) + min;
    }

    public static void main(String[] args) {
        // initialize the tile rendering engine with a window of size WIDTH x HEIGHT
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        // initialize tiles
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }

        //test
        makeRoom(world, new Position(WIDTH/2, HEIGHT/2), RANDOM.nextInt(10)+3, RANDOM.nextInt(10)+3 );

        // draws the world to the screen
        ter.renderFrame(world);
    }

}
