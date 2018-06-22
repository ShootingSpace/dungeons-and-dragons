package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import byog.lab5.HexWorld;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;

public class Game {
    TERenderer ter = new TERenderer();
    /* Feel free to change the width and height. */
    public static final int WIDTH = 80;
    public static final int HEIGHT = 30;
    public static final int BANNER = 3;

    /**
     * Method used for playing a fresh game. The game should start from the main menu.
     */
    public void playWithKeyboard() {
    }

    /**
     * Method used for autograding and testing the game code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The game should
     * behave exactly as if the user typed these characters into the game after playing
     * playWithKeyboard. If the string ends in ":q", the same world should be returned as if the
     * string did not end with q. For example "n123sss" and "n123sss:q" should return the same
     * world. However, the behavior is slightly different. After playing with "n123sss:q", the game
     * should save, and thus if we then called playWithInputString with the string "l", we'd expect
     * to get the exact same world back again, since this corresponds to loading the saved game.
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] playWithInputString(String input) {
        // TODO: Fill out this method to run the game using the input passed in,
        // and return a 2D tile representation of the world that would have been
        // drawn if the same inputs had been given to playWithKeyboard().
        TETile[][] finalWorldFrame = null;
        input = input.toUpperCase();
        if (input.charAt(0) == 'N'){
            // start new game
            String seed = parseSeed(input);
            int SEED = Integer.parseInt(seed);
            MapGenerator map = new MapGenerator(SEED, WIDTH, HEIGHT);
            TETile[][] grid = map.buildMap(BANNER);
            parseControl(map, input,seed.length() + 2);

            finalWorldFrame = map.canvas;
            // draws the world to the screen
            drawFrame(finalWorldFrame);
        }
        else if (input.charAt(0) == 'L') {
            MapGenerator map = loadMap();
            map.initCanvas(ter, BANNER);
            parseControl(map, input,1);

            finalWorldFrame = map.canvas;

            // draws the world to the screen with HUD
            drawFrame(finalWorldFrame);
        }

        return finalWorldFrame;
    }

    void parseControl(MapGenerator map, String input, int start){
        for (int i = start; i < input.length(); i++){
            play(map, input.charAt(i));
        }
    }

    void play(MapGenerator map, char cmd){
        if (cmd != ':' && cmd != 'Q'){
            map.player.move(map.canvas, cmd);
        } else {
            if (cmd == 'Q') {
                quitsaving(map);
            }
        }
    }

    private static MapGenerator loadMap() {
        File f = new File("./map.ser");
        if (f.exists()) {
            try {
                FileInputStream fs = new FileInputStream(f);
                ObjectInputStream os = new ObjectInputStream(fs);
                MapGenerator loadMap = (MapGenerator) os.readObject();
                os.close();
                return loadMap;
            } catch (FileNotFoundException e) {
                System.out.println("file not found");
                System.exit(0);
            } catch (IOException e) {
                System.out.println(e);
                System.exit(0);
            } catch (ClassNotFoundException e) {
                System.out.println("class not found");
                System.exit(0);
            }
        }

        /* In the case no World has been saved yet, we return a new one. */
        return new MapGenerator(123, WIDTH, HEIGHT);
    }

    void quitsaving(MapGenerator map){
        File f = new File("./map.ser");
        try {
            if (!f.exists()) {
                f.createNewFile();
            }
            FileOutputStream fs = new FileOutputStream(f);
            ObjectOutputStream os = new ObjectOutputStream(fs);
            os.writeObject(map);
            os.close();
            System.out.printf("Serialized data is saved in ./map.ser");
        }  catch (FileNotFoundException e) {
            System.out.println("file not found");
            System.exit(0);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(0);
        }

    }

    public String parseSeed(String input) {
        String seed = "";
        for (int i = 1; i < input.length(); i++){
            if (input.charAt(i) == 'S') {
                break;
            }
            seed += String.valueOf(input.charAt(i));
        }
        return seed;
    }
    void  drawFrame(TETile[][] world){
        while (true) {
            StdDraw.clear(new Color(0, 0, 0));
            renderCanvas(world);
            int x = (int) StdDraw.mouseX();
            int y = (int) StdDraw.mouseY();
            String description = world[x][y].description();
            StdDraw.setPenColor(Color.white);
            StdDraw.textLeft(0, HEIGHT - BANNER/16 + 1, description);
            StdDraw.line(0,HEIGHT - BANNER/16 + 0.5, WIDTH, HEIGHT - BANNER/16 + 0.5);
            StdDraw.show();
        }
    }

    void renderCanvas(TETile[][] world){
        int numXTiles = world.length;
        int numYTiles = world[0].length;

        for (int x = 0; x < numXTiles; x += 1) {
            for (int y = 0; y < numYTiles; y += 1) {
                if (world[x][y] == null) {
                    throw new IllegalArgumentException("Tile at position x=" + x + ", y=" + y
                            + " is null.");
                }
                world[x][y].draw(x, y);
            }
        }
    }
}
