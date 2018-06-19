package byog.lab5;
import org.junit.Test;
import static org.junit.Assert.*;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import javax.swing.text.Position;
import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {

    private static final long SEED = 2873123;
    private static final Random RANDOM = new Random(SEED);

    /** Position is a class with two variables p.x and p.y and no methods.*/
    static class Position {
        int x, y;
        Position(int x, int y){
            this.x = x;
            this.y = y;
        }
    }

    /** Adds a hexagon of side length s to a given position in the world
     * p specifies the lower left corner of the hexagon.*/
     public static void addHexagon(TETile[][] world, Position p, int s, TETile t){
         int[][] configs = configHexagon(p,s);
         for (int i =0; i < s * 2; i += 1) {
            drawLine(world,new Position(configs[i][0], configs[i][1]), configs[i][2],t);
         }
    }


    /** Calculate the start positions
     *  given the lower left corner position and size of the hexagon */
    private static int[][] configHexagon(Position p, int s){
        int[][] configs = new int [s *2 ][3];
        for (int i = 0; i < s ; i += 1) {
            configs[i][0] = p.x - i;
            configs[i][1] = p.y + i;
            configs[i + s][1] = p.y + s + i;
            configs[i][2] = s + i * 2;
        }
        for (int i = 0; i < s; i += 1){
            configs[i + s][0] = configs[s - 1 - i][0];
            configs[i + s][2] = configs[s - 1 - i][2];
        }
        return configs;
    }


    /** Draw one line with start position p and length l*/
    private static void drawLine(TETile[][] world, Position p, int l, TETile t){
        for (int i = 0; i <  l; i += 1) {
            world[p.x + i][p.y] = t;
        }
    }

    /** Drawing A Tesselation of Hexagons
     * consisting of 19 total hexagons. */
    public static void tesselation(TETile[][] world, Position p, int S, int s) {
//        Position ptr = new Position(p.x, p.y)
        int ox = p.x;
        int oy = p.y;
        int n = 0;
        for (int i = 0; i < S; i += 1) {
            n = i + S;
            p.y = oy - s * i;
            p.x = ox + 2 * (s - 1) * i;
            drawHexagons(world, p, s, n);
        }

        for (int i = 1; i < S; i += 1) {
            n -= 1;
            p.x = p.x + 2 * (s -1);
            p.y = p.y + s;
            drawHexagons(world, p, s, n);
        }
    }

    /** Draw multiple hexagons from start position p upwards
     * n: number of hexagons
     * s: size of each hexagons */
    static void drawHexagons(TETile[][] world, Position p, int s, int n) {
        for (int i = 0; i <  n; i += 1) {
            addHexagon(world, new Position(p.x, p.y + i * s * 2), s, randomTile());
        }
    }

    /** Picks a RANDOM tile with a 33% change of being
     *  a wall, 33% chance of being a flower, and 33%
     *  chance of being empty space.
     */
    private static TETile randomTile() {
        int tileNum = RANDOM.nextInt(10);
        switch (tileNum) {
            case 0: return Tileset.WALL;
            case 1: return Tileset.FLOWER;
            case 2: return Tileset.WATER;
            case 3: return Tileset.GRASS;
            case 4: return Tileset.LOCKED_DOOR;
            case 5: return Tileset.MOUNTAIN;
            case 6: return Tileset.SAND;
            case 7: return Tileset.PLAYER;
            case 8: return Tileset.TREE;
            case 9: return Tileset.UNLOCKED_DOOR;
            default: return Tileset.FLOOR;
        }
    }


}
