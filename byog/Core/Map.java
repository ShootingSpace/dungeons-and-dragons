package byog.Core;
import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Map implements java.io.Serializable {
    private static final long serialVersionUID = 154934524234354L;
    static long seed;
    Random randomGen;
    int width;
    int height;
    int nRoom;
    double mu;
    double sigma;
    TETile[][] canvas;
    Player player;
    Position door;


    Map(int seed, int width, int height) {
        seed = seed;
        randomGen = new Random(seed);
        this.width = width;
        this.height = height;
        nRoom = (int) RandomUtils.gaussian(randomGen, 25, 5);
        mu = 5;
        sigma = 4;
    }


    void initCanvas(TERenderer ter, int offHead) {
        // initialize the tile rendering engine with a window of size width x height
        ter.initialize(width, height + offHead, 0, 2);
    }

    TETile[][] buildMap(int offHead) {
        // initialize the tile rendering engine with a window of size width x height
        TERenderer ter = new TERenderer();
        initCanvas(ter, offHead);

        // initialize tiles
        canvas = new TETile[width][height];
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                canvas[x][y] = Tileset.NOTHING;
            }
        }

        // make rooms
        ArrayList<Room> roomsList = makeRooms(canvas, nRoom);

        // connect rooms
        connectRooms(canvas, roomsList);

        // build wall
        buildWall(canvas);

        // add door
        door = addDoor(canvas);

        // add players
        player = addPlayer(canvas, 1);

        return canvas;
    }






    /** fill the rectangular space with specific TETile
     * p specify the lower left corner */
    void makeSpace(TETile[][] world, Position p, int w, int h, TETile t) {

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                if (world[i + p.x][j + p.y] == Tileset.NOTHING) {
                    world[i + p.x][j + p.y] = t;
                }
            }
        }
    }


    /** connect two rooms*/
    void connectRooms(TETile[][] world, ArrayList<Room> roomsList) {
        for (int i = 0; i < roomsList.size() - 1; i++) {
            Room ra = roomsList.get(i);
            Room rb = roomsList.get(i + 1);
            Position pa = new Position(ra.p.x + randomGen.nextInt(ra.width),
                    ra.p.y + randomGen.nextInt(ra.height));
            Position pb = new Position(rb.p.x + randomGen.nextInt(rb.width),
                    rb.p.y + randomGen.nextInt(rb.height));
            connectPositions(world, pa, pb);
        }
    }

    /** connect two positions*/
    void connectPositions(TETile[][] world, Position a, Position b) {
        if (a.x == b.x) {
            makeSpace(world, new Position(a.x, Math.min(a.y, b.y)),
                    1, Math.abs(a.y - b.y) + 1, Tileset.HALLWAY);
        } else if (a.y == b.y) {
            makeSpace(world, new Position(Math.min(a.x, b.x), a.y),
                    Math.abs(a.x - b.x) + 1, 1, Tileset.HALLWAY);
        } else {
            Position dummy = new Position(a.x, b.y);
            connectPositions(world, a, dummy);
            connectPositions(world, b, dummy);
        }

    }

    /** build the walls*/
    void buildWall(TETile[][] world) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (world[i][j] == Tileset.NOTHING && checkNeighbours(world, i, j, 1)) {
                    world[i][j] = Tileset.WALL;
                }
            }
        }
    }


    /** check if new room overlaps with current rooms
     * return true if overlap with anyone of current rooms
     * https://stackoverflow.com/questions/306316/determine-if-two-rectangles-overlap-each-other */
    boolean overlap(ArrayList<Room> rooms, Room ra) {
        for (Room rb : rooms) {
            if (ra.x1 < rb.x2 && ra.x2 > rb.x1 && ra.y1 > rb.y2 + 1 && ra.y2 + 1 < rb.y1) {
                return  true;
            }
        }
        return false;
    }


    /** make rooms */
    ArrayList<Room> makeRooms(TETile[][] world, int num) {
        int curNumRooms = 0;
        ArrayList<Room> roomsList = new ArrayList();
        while (curNumRooms < num) {
            int px = RandomUtils.uniform(randomGen, 2, width - 2);
            int py = RandomUtils.uniform(randomGen, 2, height - 2);
            int w = (int) Math.max(Math.min(RandomUtils.gaussian(randomGen, mu, sigma),
                    width - px - 1), 2);
            int h = (int) Math.max(Math.min(RandomUtils.gaussian(randomGen, mu, sigma),
                    height - py - 1), 2);
            Room r = new Room(curNumRooms, new Position(px, py), w, h);
            if (!overlap(roomsList, r)) {
                roomsList.add(r);
                makeSpace(world, new Position(px, py), w, h, Tileset.FLOOR);
                curNumRooms += 1;
            }
        }
        Collections.sort(roomsList);
        return roomsList;
    }

    /** Add a locked door */
    Position addDoor(TETile[][] world) {
        boolean added = false;
        int startx = 0;
        int starty = 0;
        while (!added) {
            startx = (int) RandomUtils.gaussian(randomGen, width / 2, width / 5);
            starty = 1;
            while (world[startx][starty] != Tileset.WALL) {
                starty += 1;
            }
            if (checkNeighbours(world, startx, starty, 2)) {
                world[startx][starty] = Tileset.LOCKED_DOOR;
                added = true;
            }
        }
        return new Position(startx, starty);
    }

    Player addPlayer(TETile[][] world, int numPlayers) {
        int added = 0;
        int px = 0;
        int py = 0;
        while (added < numPlayers) {
            px = RandomUtils.uniform(randomGen, 2, width - 2);
            py = RandomUtils.uniform(randomGen, 2, height - 2);
            if (world[px][py] == Tileset.FLOOR) {
                world[px][py] = Tileset.PLAYER;
                added += 1;
            }
        }
        return new Player(new Position(px, py));
    }


    /** Check a given position is a valid position for wall or closed door
     * determined by the number of Tileset.FLOOR in all eight neighbours */
    boolean checkNeighbours(TETile[][] world, int x, int y, int numFloors) {
        int checked = 0;
        int xLeft = Math.max(0, x - 1);
        int xRight = Math.min(x + 1, width - 1);
        int yUp = Math.min(y + 1, height - 1);
        int yLow = Math.max(0, y - 1);
        for (int i = xLeft; i <= xRight; i++) {
            for (int j = yLow; j <= yUp; j++) {
                if (world[i][j] == Tileset.FLOOR || world[i][j] == Tileset.HALLWAY) {
                    checked += 1;
                    if (checked == numFloors) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void main(String[] args) {
        int s = 2018;

        Random randomGen = new Random();
        int width = 80;
        int height = 40;
        int nRoom = RandomUtils.poisson(randomGen, 25);
        double mu = 5;
        double sigma = 4;
        Map map = new Map(s, width, height);

        // initialize the tile rendering engine with a window of size width x height
        TERenderer ter = new TERenderer();
        ter.initialize(width, height);

        // initialize tiles
        TETile[][] world = new TETile[width][height];
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }

        // make rooms
        ArrayList<Room> roomsList = map.makeRooms(world, nRoom);

        // connect rooms
        map.connectRooms(world, roomsList);

        // build wall
        map.buildWall(world);

        // add door
        map.addDoor(world);

        // add players
        map.addPlayer(world, 1);

        // draws the world to the screen
        ter.renderFrame(world);
    }

}
