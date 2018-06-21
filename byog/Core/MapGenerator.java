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

        }


    }

        }

    }

        }
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


    public static void main(String[] args) {
        // initialize the tile rendering engine with a window of size WIDTH x HEIGHT
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        ArrayList<Room> roomsList = new ArrayList();
        int curNumRooms = 0;
        int[][] roomNos = new int[WIDTH][HEIGHT];

        // initialize tiles
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }

        // make rooms
        while(curNumRooms < NROOM){
            int px = RANDOM.nextInt(WIDTH - 5) + 3;
            int py = RANDOM.nextInt(HEIGHT - 5) + 3;
            int width = Math.max(Math.min(RANDOM.nextInt(10) + 1,WIDTH - px - 1), 2);
            int height = Math.max(Math.min(RANDOM.nextInt(5) + 1,HEIGHT - py - 1), 2);
            Room r = new Room(curNumRooms, new Position(px, py), width, height);
            if (!overlap(roomsList, r)){
                roomsList.add(r);
                makeSpace(world, new Position(px, py), width, height, Tileset.FLOOR);
                curNumRooms += 1;
            }

        }

        Collections.sort(roomsList);


        // draws the world to the screen
        ter.renderFrame(world);
    }

}
