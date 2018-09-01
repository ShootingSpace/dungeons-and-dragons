package byog.Core;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Font;
import java.awt.Color;
import java.io.FileOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

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
        StdDraw.setCanvasSize(WIDTH * 16, HEIGHT * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, WIDTH);
        StdDraw.setYscale(0, HEIGHT);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();

        drawStarGUI();

        userSelections();

    }

    public void userSelections() {

        while (!StdDraw.hasNextKeyTyped()) {
            continue;
        }

        char key = Character.toUpperCase(StdDraw.nextKeyTyped());
        if (key == 'N') {
            // the user should be prompted to enter a “random seed”
            String seedString = "Please enter a random seed (end with 'S'): ";
            drawString(seedString);
            String input = solicitNCharsInput(seedString, 'S');
            int s = Integer.parseInt(input.substring(0, input.length() - 1));
            Map map = new Map(s, WIDTH, HEIGHT);
            TETile[][] grid = map.buildMap(BANNER);
            roundPlay(map);

        } else if (key == 'L') {
            Map map = loadMap();
            map.initCanvas(ter, BANNER);
            roundPlay(map);
        } else if (key == 'Q') {
            drawString("GOOD BYE!");
        }

    }

    void roundPlay(Map map) {
        while (true) {
            StdDraw.clear(new Color(0, 0, 0));
            renderCanvas(map.canvas);
            int x = (int) StdDraw.mouseX();
            int y = (int) StdDraw.mouseY();
            String description = map.canvas[x][y].description();
            StdDraw.setPenColor(Color.white);
            StdDraw.textLeft(0, HEIGHT - BANNER / 16 + 1, description);
            StdDraw.line(0, HEIGHT - BANNER / 16 + 0.5, WIDTH, HEIGHT - BANNER / 16 + 0.5);
            StdDraw.show();
            if (!StdDraw.hasNextKeyTyped()) {
                continue;
            }
            char key = Character.toUpperCase(StdDraw.nextKeyTyped());
            boolean quit = play(map, key);
            if (quit) {
                break;
            }
            StdDraw.show();
        }

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
        // DONE: Fill out this method to run the game using the input passed in,
        // and return a 2D tile representation of the world that would have been
        // drawn if the same inputs had been given to playWithKeyboard().
        TETile[][] finalWorldFrame = null;
        input = input.toUpperCase();
        if (input.charAt(0) == 'N') {
            // start new game
            String seed = parseSeed(input);
            int s = Integer.parseInt(seed);
            Map map = new Map(s, WIDTH, HEIGHT);
            TETile[][] grid = map.buildMap(BANNER);
            parseControl(map, input, seed.length() + 2);

            finalWorldFrame = map.canvas;
            // draws the world to the screen
            drawFrame(finalWorldFrame);
        } else if (input.charAt(0) == 'L') {
            Map map = loadMap();
            map.initCanvas(ter, BANNER);
            parseControl(map, input, 1);

            finalWorldFrame = map.canvas;

            // draws the world to the screen with HUD
            drawFrame(finalWorldFrame);
        }

        return finalWorldFrame;
    }

    void parseControl(Map map, String input, int start) {
        for (int i = start; i < input.length(); i++) {
            play(map, input.charAt(i));
        }
    }

    /** return true if quit game */
    boolean play(Map map, char cmd) {

        if (cmd != ':' && cmd != 'Q') {
            map.player.move(map.canvas, cmd);
        } else {
            if (cmd == 'Q') {
                quitsaving(map);
                return true;
            }
        }
        return false;
    }

    private static Map loadMap() {
        File f = new File("./map.ser");
        if (f.exists()) {
            try {
                FileInputStream fs = new FileInputStream(f);
                ObjectInputStream os = new ObjectInputStream(fs);
                Map loadMap = (Map) os.readObject();
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
        return new Map(123, WIDTH, HEIGHT);
    }

    void quitsaving(Map map) {
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
        for (int i = 1; i < input.length(); i++) {
            if (input.charAt(i) == 'S') {
                break;
            }
            seed += String.valueOf(input.charAt(i));
        }
        return seed;
    }


    public String solicitNCharsInput(String prefix, char stop) {
        String input = "";
        String display = prefix + input;
        drawString(display);

        while (Character.toUpperCase(display.charAt(display.length() - 1)) != stop) {
            if (!StdDraw.hasNextKeyTyped()) {
                continue;
            }
            char key = StdDraw.nextKeyTyped();
            input += String.valueOf(key);
            display = prefix + input;
            drawString(display);
        }
        StdDraw.pause(500);
        return input;
    }

    void  drawFrame(TETile[][] world) {
        while (true) {
            StdDraw.clear(new Color(0, 0, 0));
            renderCanvas(world);
            int x = (int) StdDraw.mouseX();
            int y = (int) StdDraw.mouseY();
            String description = world[x][y].description();
            StdDraw.setPenColor(Color.white);
            StdDraw.textLeft(0, HEIGHT - BANNER / 16 + 1, description);
            StdDraw.line(0, HEIGHT - BANNER / 16 + 0.5, WIDTH, HEIGHT - BANNER / 16 + 0.5);
            StdDraw.show();
        }
    }

    void renderCanvas(TETile[][] world) {
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

    public void drawString(String s) {

        StdDraw.clear();
        StdDraw.clear(Color.black);

        // Draw the actual text
        Font bigFont = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(bigFont);
        StdDraw.setPenColor(Color.white);
        StdDraw.text(WIDTH / 2, HEIGHT / 2, s);
        StdDraw.show();
    }

    public void drawStarGUI() {
        int midWidth = WIDTH / 2;

        StdDraw.clear();
        StdDraw.clear(Color.black);

        Font bigFont = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(bigFont);
        StdDraw.setPenColor(Color.white);
        StdDraw.text(midWidth, HEIGHT - 10, "Dungeons & Dragons");

        Font smallFont = new Font("Monaco", Font.BOLD, 20);
        StdDraw.setFont(smallFont);
        StdDraw.text(midWidth, HEIGHT - 15, "New Gmae (N)");
        StdDraw.text(midWidth, HEIGHT - 17, "Load Game (L)");
        StdDraw.text(midWidth, HEIGHT - 19, "Quit (Q)");

        StdDraw.show();
    }
}
