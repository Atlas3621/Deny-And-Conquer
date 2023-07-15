import javafx.scene.paint.Color;

/**
 * A token class to represent "FILL" tokens
 */
public class FillToken {
    private int squareNum;
    private Color drawColor;

    /**
     * @param color The color to fill the square with on the other screen
     * @param squareNum The index of the square in the list
     */
    public FillToken (Color color, int squareNum) {
        this.drawColor = color;
        this.squareNum = squareNum;
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
}
