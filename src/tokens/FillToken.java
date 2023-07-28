package tokens;

import javafx.scene.paint.Color;

/**
 * A token class to represent "FILL" tokens. These are sent from the Server -> All Clients when a Client has filled up a square.
 * This token tells the client to update the representation of the board on its side.
 */
public class FillToken {
    private int squareNum;
    private Color drawColor;
    private LiftToken lift;


    /**
     * @param color The color to fill the square with on the other screen
     * @param squareNum The index of the square in the list
     */
    public FillToken (Color color, int squareNum) {
        this.drawColor = color;
        this.squareNum = squareNum;
    }

    /**
     * @param tokenString A String representation of this token transmitted over, say, TCP
     */
    public FillToken (String tokenString) {
        this.drawColor = Color.web(tokenString.substring(6, 14));
        this.squareNum = Integer.parseInt(tokenString.substring(14, 16));
    }

    /**
     * Converts the Fill Token to a String Representation
     * @return A String Representation of the Token (Useful for Sending Across TCP)
     */
    @Override
    public String toString() {
        String sString = String.valueOf(squareNum);

        if (String.valueOf(this.squareNum).length() == 1) {
            sString = "0" + squareNum;
        }

        return "FILL" + drawColor + sString;
    }

    // These getters here are useful for our "new" method parsing with the constructor
    public Color getDrawColor() {
        return drawColor;
    }

    public int getSquareNum() {
        return squareNum;
    }
}
