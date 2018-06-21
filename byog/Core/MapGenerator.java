package byog.Core;
import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class MapGenerator {
    private static final Random RANDOM = new Random();
    private static final int WIDTH = 80;
    private static final int HEIGHT = 40;
    private static final int NROOM = 25;

    /** Position is a class with two variables p.x and p.y and no methods.*/
    static class Position {
        int x, y;
        Position(int x, int y){
            this.x = x;
            this.y = y;
        }
    }


    /** draw the floor space of a room
     *
     */
    static void makeSpace(TETile[][] world, Position p, int width, int height, TETile t){

        for (int i=0; i<width; i++){
            for (int j=0; j<height; j++){
                if (world[i+p.x][j+p.y] == Tileset.NOTHING){
                    world[i+p.x][j+p.y] = t;
                }
            }
        }
    }


    /** connect two rooms*/
    static void connectRooms(TETile[][] world, ArrayList<Room> roomsList){
        for (int i=0; i < roomsList.size() - 1; i++){
            Room ra = roomsList.get(i);
            Room rb = roomsList.get(i+1);
            Position pa = new Position(ra.p.x + RANDOM.nextInt(ra.width), ra.p.y + RANDOM.nextInt(ra.height));
            Position pb = new Position(rb.p.x + RANDOM.nextInt(rb.width), rb.p.y + RANDOM.nextInt(rb.height));
            connectPositions(world, pa, pb);
        }
    }

    /** connect two positions*/
    static void connectPositions(TETile[][] world, Position a, Position b){
        if (a.x == b.x){
            makeSpace(world, new Position(a.x, Math.min(a.y, b.y)), 1, Math.abs(a.y - b.y) + 1, Tileset.HALLWAY);
        } else if (a.y == b.y) {
            makeSpace(world, new Position(Math.min(a.x, b.x), a.y), Math.abs(a.x - b.x) + 1, 1, Tileset.HALLWAY);
        } else {
            Position dummy = new Position(a.x, b.y);
            connectPositions(world, a, dummy);
            connectPositions(world, b, dummy);
        }

    }

    /** build the walls*/
    static void buildWall(TETile[][] world){
        for (int i=0; i < WIDTH; i++){
            for (int j=0; j<HEIGHT; j++){
                if (world[i][j] == Tileset.NOTHING && checkNeighbours(world, i, j, 1)){
                    world[i][j] = Tileset.WALL;
                }
            }
        }
    }


    /** check if new room overlaps with current rooms
     * return true if overlap with anyone of current rooms
     * https://stackoverflow.com/questions/306316/determine-if-two-rectangles-overlap-each-other */
    static boolean overlap(ArrayList<Room> rooms, Room ra){
        for (Room rb : rooms){
            if (ra.x1 < rb.x2 && ra.x2 > rb.x1 && ra.y1 > rb.y2 + 1 && ra.y2 + 1 < rb.y1){
                return  true;
            }
        }
        return false;
    }


    /** make rooms */
    static ArrayList<Room> makeRooms(TETile[][] world,int num){
        int curNumRooms = 0;
        ArrayList<Room> roomsList = new ArrayList();
        while(curNumRooms < num){
            int px = RandomUtils.uniform(RANDOM,2,WIDTH - 2);
            int py = RandomUtils.uniform(RANDOM,2,HEIGHT - 2);
            int width = (int) Math.max(Math.min(RandomUtils.gaussian(RANDOM,mu,sigma), WIDTH - px - 1), 2);
            int height = (int) Math.max(Math.min(RandomUtils.gaussian(RANDOM,mu,sigma), HEIGHT - py - 1),2);
            Room r = new Room(curNumRooms, new Position(px, py), width, height);
            if (!overlap(roomsList, r)){
                roomsList.add(r);
                makeSpace(world, new Position(px, py), width, height, Tileset.FLOOR);
                curNumRooms += 1;
            }
        }
        Collections.sort(roomsList);
        return roomsList;
    }

    /** Add a locked door */
    static void addDoor(TETile[][] world){
        boolean added = false;
        while (!added) {
            int startx = (int) RandomUtils.gaussian(RANDOM, WIDTH/2, WIDTH/5);
            int starty = 1;
            while (world[startx][starty]!=Tileset.WALL){
                starty += 1;
            }
            if (checkNeighbours(world, startx, starty, 2)){
                world[startx][starty] = Tileset.LOCKED_DOOR;
                added = true;
            }
        }
    }


    /** Check a given position is a valid position for wall or closed door
     * determined by the number of Tileset.FLOOR in all eight neighbours */
    static boolean checkNeighbours(TETile[][] world, int x, int y, int numFloors){
        int checked = 0;
        int xLeft = Math.max(0,x - 1);
        int xRight = Math.min(x + 1,WIDTH - 1);
        int yUp = Math.min(y + 1, HEIGHT - 1);
        int yLow = Math.max(0, y - 1);
        for (int i = xLeft; i <= xRight; i++){
            for (int j = yLow; j <= yUp; j++){
                if (world[i][j] == Tileset.FLOOR || world[i][j] == Tileset.HALLWAY){
                    checked += 1;
                    if (checked == numFloors){
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static void main(String[] args) {
//        SEED = 2018;
        RANDOM = new Random();
        WIDTH = 80;
        HEIGHT = 40;
        NROOM = RandomUtils.poisson(RANDOM, 25);
        System.out.println(NROOM);
        mu = 5;
        sigma = 4;

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

        // make rooms
        ArrayList<Room> roomsList = makeRooms(world, NROOM);

        //connect rooms
        connectRooms(world, roomsList);

        // build wall
        buildWall(world);

        // add door
        addDoor(world);

        // draws the world to the screen
        ter.renderFrame(world);
    }

}
