package byog.lab6;

import edu.princeton.cs.algs4.ST;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.Color;
import java.awt.Font;
import java.util.Random;
import java.util.*;
public class MemoryGame {
    private int width;
    private int height;
    private int round;
    private Random rand;
    private boolean gameOver;
    private boolean playerTurn;
    private static final char[] CHARACTERS = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    private static final String[] ENCOURAGEMENT = {"You can do this!", "I believe in you!",
                                                   "You got this!", "You're a star!", "Go Bears!",
                                                   "Too easy for you!", "Wow, so impressive!"};

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Please enter a seed");
            return;
        }

        int seed = Integer.parseInt(args[0]);
        MemoryGame game = new MemoryGame(40, 40);
        game.startGame();
    }

    public MemoryGame(int width, int height) {
        /* Sets up StdDraw so that it has a width by height grid of 16 by 16 squares as its canvas
         * Also sets up the scale so the top left is (0,0) and the bottom right is (width, height)
         */
        this.width = width;
        this.height = height;
        StdDraw.setCanvasSize(this.width * 16, this.height * 16);
        Font font = new Font("Monaco", Font.BOLD, 30);
        StdDraw.setFont(font);
        StdDraw.setXscale(0, this.width);
        StdDraw.setYscale(0, this.height);
        StdDraw.clear(Color.BLACK);
        StdDraw.enableDoubleBuffering();

        //TODO: Initialize random number generator
        rand = new Random(width);
    }

    public String generateRandomString(int n) {
        //TODO: Generate random string of letters of length n
        if (n < 1) throw new IllegalArgumentException();
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < n; i++){
            buffer.append(CHARACTERS[rand.nextInt(CHARACTERS.length)]);
        }
        return buffer.toString();
    }

    /** Clears the canvas, sets the font to be large and bold (size 30 is appropriate),
     * draws the input string so that it is centered on the canvas, and then shows the canvas on the screen. */
    public void drawFrame(String s, int mode) {
        //TODO: Take the string and display it in the center of the screen
        //TODO: If game is not over, display relevant game information at the top of the screen
        StdDraw.clear();
        StdDraw.line(0, height*9/10, width, height*9/10);
        StdDraw.setFont(new Font("TimesRoman", Font.BOLD, 30));
        StdDraw.text(width/2, height/2, s);
        StdDraw.text(width*0.5/3, height*9.5/10, "Round: "+ round);
        if (mode == 1) {
            StdDraw.text(width*1.2/3, height*9.5/10, "Watch!");
        } else if (mode == 2) {
            StdDraw.text(width*1.2/3, height*9.5/10, "Type!");
        }
        StdDraw.text(width*2.2/3, height*9.5/10, ENCOURAGEMENT[rand.nextInt(ENCOURAGEMENT.length)]);
        StdDraw.show();
    }

    /** takes the input string and displays one character at a time centered on the screen.
     * Each character should be visible on the screen for 1 second
     * and there should be a brief 0.5 second break between characters where the screen is blank. */
    public void flashSequence(String letters) {
        //TODO: Display each character in letters, making sure to blank the screen between letters
        for (Character x : letters.toCharArray()){
            drawFrame(x.toString(), 0);
            StdDraw.pause(1000);
            drawFrame("",0);
            StdDraw.pause(500);
        }
    }

    /** reads n keystrokes and returns the string corresponding to those keystrokes.
     *  the string built up so far should appear centered on the screen as keys are being typed by the user
     *  so that they can see what they’ve hit so far.*/
    public String solicitNCharsInput(int n) {
        //TODO: Read n letters of player input
        StringBuilder keys = new StringBuilder();
        for (int i = 0; i<n; i++){
            while(!StdDraw.hasNextKeyTyped()){}
            if(StdDraw.hasNextKeyTyped()){
                keys.append(StdDraw.nextKeyTyped());
            }
            drawFrame(keys.toString(), 2);
        }
        return keys.toString();
    }

    /** 1. Start the game at round 1
        2. Display the message “Round: “ followed by the round number in the center of the screen
        3. Generate a random string of length equal to the current round number
        4. Display the random string one letter at a time
        5. Wait for the player to type in a string the same length as the target string
        6. Check to see if the player got it correct
            * If they got it correct, repeat from step 2 after increasing the round by 1
            * If they got it wrong, end the game and display the message “Game Over! You made it to round:”
     *          followed by the round number they failed in the center of the screen
     */
    public void startGame() {
        //TODO: Set any relevant variables before the game starts
        String display;
        String typed;
        //TODO: Establish Game loop
        round = 1;
        while (true) {
            drawFrame("Round: "+ round, 0);
            display = generateRandomString(round);
            drawFrame(display, 1);
            typed = solicitNCharsInput(round);
            if (typed.equals(display)){
                round += 1;
            } else { break; }
        }
        drawFrame("Game Over! You made it to round:"+ round, 0);
    }
}
