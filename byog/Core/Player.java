package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

class Player implements java.io.Serializable {
    private static final long serialVersionUID = 123123123123123L;
    Position p;

    Player(Position p) {
        this.p = p;
    }

    void move(TETile[][] world, char cmd) {
        if (cmd == 'W') {
            if (p.y < world[0].length - 1 && !world[p.x][p.y + 1].equals(Tileset.WALL)) {
                world[p.x][p.y] = Tileset.FOOTPRINT;
                p.y += 1;
            }
        } else if (cmd == 'S') {
            if (p.y > 0 && !world[p.x][p.y - 1].equals(Tileset.WALL)) {
                world[p.x][p.y] = Tileset.FOOTPRINT;
                p.y -= 1;
            }
        } else if (cmd == 'A') {
            if (p.x > 0 && !world[p.x - 1][p.y].equals(Tileset.WALL)) {
                world[p.x][p.y] = Tileset.FOOTPRINT;
                p.x -= 1;
            }
        } else if (cmd == 'D') {
            if (p.x < world.length - 1 && !world[p.x + 1][p.y].equals(Tileset.WALL)) {
                world[p.x][p.y] = Tileset.FOOTPRINT;
                p.x += 1;
            }
        }
        world[p.x][p.y] = Tileset.PLAYER;

    }

}
