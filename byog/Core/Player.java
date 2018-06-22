package byog.Core;

import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

class Player implements java.io.Serializable {
    private static final long serialVersionUID = 123123123123123L;
    Position p;

    Player(Position p){
        this.p = p;
    }

    void move(TETile[][] world, char cmd){
        if (cmd == 'W'){
            if (world[p.x][p.y + 1] != Tileset.WALL){
                world[p.x][p.y] = Tileset.FOOTPRINT;
                p.y += 1;
            }
        } else if (cmd == 'S') {
            if (world[p.x][p.y - 1] != Tileset.WALL) {
                world[p.x][p.y] = Tileset.FOOTPRINT;
                p.y -= 1;
            }
        } else if (cmd == 'A'){
            if (world[p.x - 1][p.y] != Tileset.WALL) {
                world[p.x][p.y] = Tileset.FOOTPRINT;
                p.x -= 1;
            }
        } else if (cmd == 'D'){
            if (world[p.x + 1][p.y] != Tileset.WALL) {
                world[p.x][p.y] = Tileset.FOOTPRINT;
                p.x += 1;
            }
        }
        world[p.x][p.y] = Tileset.PLAYER;

    }

}