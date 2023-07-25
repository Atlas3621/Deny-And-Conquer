package tokens;

import javafx.scene.paint.Color;

/**
 * A token class to represent "LIFT" tokens. These are sent from Client -> Server to indicate a user has lifted their mouse.
 * The server will then send a "CLEAR" token -> Client to tell the client to update its canvas.
 * TODO: See ClearToken for note about possible redundancy
 */
public class LiftToken {
    private int squareNum;
    private Color drawColor;

    /**
     * @param color The color lifting off the mouse, used for verification purposes.
     * @param squareNum The index of the square in the list
     */
    public LiftToken (Color color, int squareNum) {
        this.drawColor = color;
        this.squareNum = squareNum;
    }

    /**
     * @param tokenString A String representation of this token transmitted over, say, TCP
     */
    public LiftToken (String tokenString) {
        this.drawColor = Color.web(tokenString.substring(6, 14));
        this.squareNum = Integer.parseInt(tokenString.substring(14, 16));
    }

    /**
     * Converts the Lift Token to a String Representation
     * @return A String Representation of the Token (Useful for Sending Across TCP)
     */
    @Override
    public String toString() {
        String sString = String.valueOf(squareNum);

        if (String.valueOf(this.squareNum).length() == 1) {
            sString = "0" + squareNum;
        }

        return "LIFT" + drawColor + sString;
    }

    // These getters here are useful for our "new" method parsing with the constructor
    public int getSquareNum() {
        return squareNum;
    }

    public Color getDrawColor() {
        return drawColor;
    }
}
