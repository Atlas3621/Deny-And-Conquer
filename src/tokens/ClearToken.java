package tokens;

/**
 * A token used to send from Deny and Conquer Server -> Client to let the client know to clear a square.
 * TODO: Possibly redundant with LiftToken. Look at combining the two.
 */
public class ClearToken {
    private int squareNum;

    /**
     * @param squareNum The index of the square in the list to clear
     */
    public ClearToken (int squareNum) {
        this.squareNum = squareNum;
    }

    /**
     * @param tokenString A String representation of this token transmitted over, say, TCP
     */
    public ClearToken (String tokenString) {
        this.squareNum = Integer.parseInt(tokenString.substring(5,7));
    }

    /**
     * Converts the Clear Token to a String Representation
     * @return A String Representation of the Token (Useful for Sending Across TCP)
     */
    @Override
    public String toString() {
        String sString = String.valueOf(squareNum);

        if (String.valueOf(this.squareNum).length() == 1) {
            sString = "0" + squareNum;
        }

        return "CLEAR" + sString;
    }

    // These getters here are useful for our "new" method parsing with the constructor
    public int getSquareNum() {
        return squareNum;
    }
}