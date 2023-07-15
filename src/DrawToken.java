import javafx.scene.paint.Color;

/**
 * A token class to represent "DRAW" tokens
 */
public class DrawToken {
    private int squareNum, xCord, yCord;
    private Color drawColor;

    /**
     * @param color The color to draw on the other screen
     * @param squareNum The index of the square in the list
     * @param xCord The x Coordinate to draw the pixel on in the square
     * @param yCord The y Coordinate to draw the pixel on in the square
     */
    public DrawToken (Color color, int squareNum, int xCord, int yCord) {
        this.drawColor = color;

        this.squareNum = squareNum;
        this.xCord = xCord;
        this.yCord = yCord;
    }

    /**
     * Converts the Draw Token to a String Representation
     * @return A String Representation of the Token (Useful for Sending Across TCP)
     */
    @Override
    public String toString() {
        String xString = String.valueOf(xCord);
        String yString = String.valueOf(yCord);
        String sString = String.valueOf(squareNum);

        if (String.valueOf(this.squareNum).length() == 1) {
            sString = "0" + squareNum;
        }

        if (String.valueOf(this.xCord).length() == 1) {
            xString = "0" + xCord;
        }

        if (String.valueOf(this.yCord).length() == 1) {
            yString = "0" + yCord;
        }

        return "DRAW" + drawColor + sString + xString + yString;
    }
}
