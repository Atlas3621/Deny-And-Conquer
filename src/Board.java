import java.util.ArrayList;
import java.util.Hashtable;

import javafx.scene.paint.Color;

/**
 * The "Board" class is used as a server-side representation of the deny-and-conquer game board.
 * We can view it as a single source of truth in this game as well, seeing as the clients take their information from here.
 * Why not use an array of CounterCanvas'? Well, they contain a lot of unnecessary information. GamePieces are Simpler.
 */
public class Board {
    private final ArrayList<GamePiece> grid; // The arraylist of game-pieces that we ultimately use as the grid.
    private final Hashtable<Color, Integer> squaresFilledDict = new Hashtable<Color, Integer>(); // A dictionary that tracks how many squares in the grid each color has filled
    private final int totalSquares; // Total # of squares in the grid

    /**
     * @param canvasSize The size of each individual canvas (they must be a square, else this all breaks)
     * @param xSize The number of squares in the x direction (i.e. grid width)
     * @param ySize The number of squares in the y direction (i.e. grid height)
     * @param lineWidth The width of the border within the canvas (used for calculating area that needs to be filled).
     */
    Board(int canvasSize, int xSize, int ySize, int lineWidth) {
        this.grid = new ArrayList<GamePiece>();
        this.totalSquares = xSize * ySize;

        int twiceLineWidth = lineWidth * 2;
        int canvasArea = (canvasSize - twiceLineWidth) * (canvasSize - twiceLineWidth);

        for (int i = 0; i < xSize; i++) {
            for (int j = 0; j < ySize; j++) {
                grid.add(new GamePiece(canvasArea));
            }
        }
    }

    /**
     * A function that updates our server-side representation with a new pixel that a color has drawn
     * (See GamePiece below for why the filledByDict exists)
     * @param gridPos The position of the square in the array that we want to add a pixel on for a color
     * @param color The color who is adding a pixel to the square
     */
    public void drawPixel(int gridPos, Color color) {
        // Updating the representation of how much things are filled in our GamePiece ArrayList
        GamePiece curPiece = grid.get(gridPos);
        curPiece.filledByDict.put(color, curPiece.filledByDict.getOrDefault(color, 0) + 1);
    }

    /**
     * A function to check whether a color has filled the majority of a given square
     * @param color The color that we want to check in the dictionary
     * @param gridPos The position of the square we want to check
     * @return True if the color has filled a majority (>=50%) of the pixels in the square, False otherwise
     */
    public boolean checkFilled(Color color, int gridPos) {
        // Checking whether a given color has filled a majority of the entire square
        GamePiece curPiece = grid.get(gridPos);
        Integer filledByThisColor = curPiece.filledByDict.get(color);
        return filledByThisColor != null && filledByThisColor > (0.5) * curPiece.totalArea;
    }

    /**
     * A function to check whether a color can draw on a given square (i.e. whether it currently drawing on it or not)
     * @param color The color we want to check against a given square
     * @param gridPos The square whose draw-ability we want to examine
     * @return True if the given color can draw on the square, False otherwise
     */
    public boolean isAvailableToDraw(Color color, int gridPos) {
        GamePiece curPiece = grid.get(gridPos);
        return !(curPiece.filled) && (!(curPiece.isClaimed) || (curPiece.claimedBy.equals(color)));
    }

    /**
     * A function that (in our representation of the board on the server-side) sets a square as claimed for a color
     * @param color The color claiming the square
     * @param gridPos The square the color is claiming
     */
    public void setClaimed(Color color, int gridPos) {
        GamePiece curPiece = grid.get(gridPos);
        curPiece.isClaimed = true;
        curPiece.claimedBy = color;
    }

    /**
     * A function that (in our representation of the board on the server-side) sets a square as filled for a color
     * @param color The color that is filling the square
     * @param gridPos The square the color is filling
     */
    public void setFilled(Color color, int gridPos) {
        // Setting Filled Properties on Piece
        GamePiece curPiece = grid.get(gridPos);
        curPiece.filled = true;
        curPiece.filledBy = color;

        // Updating Dictionary
        squaresFilledDict.put(color, squaresFilledDict.getOrDefault(color, 0) + 1);
    }

    /**
     * A function that checks of a winner exists
     * @return True if one does (i.e. >=50% of squares are controlled by them), False otherwise
     */
    public boolean winnerExists() {
        return squaresFilledDict.values().stream().anyMatch(x -> x >= Math.ceil(totalSquares * 0.5));
    }

    /**
     * A function that checks the color of the winner (only called if we know one exists)
     * @return The color that controls >=50% of total squares
     */
    public Color colorOfWinner() {
        final Color[] toReturn = new Color[1];
        squaresFilledDict.forEach((x, y) -> {
            if (squaresFilledDict.get(x) >= Math.ceil(totalSquares * 0.5)) {
                toReturn[0] = x;
            }
        });
        return toReturn[0];
    }

    /**
     * A nice helper function to reset some game piece after a color clears off of it.
     * @param gridPos The game piece that we want to reset
     */
    public void resetPiece(int gridPos) {
        grid.get(gridPos).reset();
    }

    /**
     * The GamePiece class that acts as an analogue for the CounterCanvas class on the Server-Side.
     * It contains information more relevant to the server, and doesn't contain unnecessary information.
     * This allows our server to broadcast information to all clients about the state of the game
     */
    private class GamePiece {
        public Color filledBy, claimedBy; // Self-explanatory, who filled/claimed this GamePiece.
        public int totalArea; // The total area (# pixels) of this game piece
        public boolean filled, isClaimed; // Whether this game piece has been filled or claimed
        public Hashtable<Color, Integer> filledByDict; // This is a dictionary so that you could expand the shared object later

        /**
         * @param totalArea The total area (# pixels) contained in this game piece
         */
        private GamePiece (int totalArea) {
            this.filledBy = null;
            this.filled = false;
            this.totalArea = totalArea;
            this.filledByDict = new Hashtable<Color, Integer>();
        }

        /**
         * A simple helper function to reset a GamePiece.
         * Used when we are clearing one square in the grid after a user lifted off of it.
         */
        public void reset() {
            this.filledBy = null;
            this.claimedBy = null;

            this.filled = false;
            this.isClaimed = false;

            this.filledByDict = new Hashtable<Color, Integer>();
        }
    }
}
