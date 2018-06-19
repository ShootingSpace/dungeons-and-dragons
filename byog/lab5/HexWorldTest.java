package byog.lab5;
import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.util.Random;

public class HexWorldTest {
    private static final int WIDTH = 80;
    private static final int HEIGHT = 40;

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

        HexWorld.addHexagon(world,new HexWorld.Position(5, HEIGHT/2),4,Tileset.PLAYER);
        HexWorld.drawHexagons(world,new HexWorld.Position(15, HEIGHT*1/3),3, 3);
        HexWorld.tesselation(world,new HexWorld.Position(WIDTH/2, HEIGHT*1/3),3, 3);

        // draws the world to the screen
        ter.renderFrame(world);

    }
}
