package byog.lab6;

import edu.princeton.cs.introcs.StdDraw;

public class MemoryGameTest {

    public static void main(String[] args) {
        MemoryGame mg = new MemoryGame(20, 20);
        String x = mg.generateRandomString(5);
//        mg.flashSequence(x);
        mg.solicitNCharsInput(10);
    }


}
