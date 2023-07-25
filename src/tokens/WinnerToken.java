package tokens;

import javafx.scene.paint.Color;

/**
 * A token class to represent "WINNER" tokens. These tokens are used by the server and sent to the client.
 * Upon receipt of such a token, the client will display the game end screen.
 * TODO: Look at adding a button to begin a new game to the game end screen, and maybe converting it to FXML.
 */
public class WinnerToken {
    private Color drawColor;

    /**
     * @param color The color that has won the game of deny and conquer!
     */
    public WinnerToken (Color color) {
        this.drawColor = color;
    }

    /**
     * @param tokenString A String representation of this token transmitted over, say, TCP
     */
    public WinnerToken (String tokenString) {
        this.drawColor = Color.web(tokenString.substring(6, 14));
    }

    /**
     * Converts the Winner Token to a String Representation
     * @return A String Representation of the Token (Useful for Sending Across TCP)
     */
    @Override
    public String toString() {
        return "WINNER" + drawColor;
    }

    // These getters here are useful for our "new" method parsing with the constructor
    public Color getDrawColor() {
        return drawColor;
    }
}
