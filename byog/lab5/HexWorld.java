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
}
