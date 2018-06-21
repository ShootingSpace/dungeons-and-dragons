package byog.Core;

import byog.TileEngine.TETile;
import java.util.Comparator;
import java.util.Random;

/** a room is a rectangular wall with one or two breaks,
 * the inside space is floor
 */
public class Room implements Comparable<Room>{
    int no;
    MapGenerator.Position p;
    int x1;
    int x2;
    int y1;
    int y2;
    int width;
    int height;
    int dist;

    Room(int no, MapGenerator.Position p, int width, int height) {
        this.no = no;
        this.p = p;
        this.width = width;
        this.height = height;
        dist = p.x * p.x + p.y * p.y;
        x1 = p.x;
        x2 = p.x + width;
        y1 = p.y;
        y2 = p.y + height;
    }

    @Override
    public int compareTo(Room uddaRoom) {
        return this.x2 - uddaRoom.x1;
    }

}
