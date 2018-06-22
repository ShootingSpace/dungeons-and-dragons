package byog.Core;

/** Position is a class with two variables p.x and p.y and no methods.*/
public class Position implements java.io.Serializable {
    private static final long serialVersionUID = 45498234798734234L;
    int x, y;
    Position(int x, int y){
        this.x = x;
        this.y = y;
    }
}
