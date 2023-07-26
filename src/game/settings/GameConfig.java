package game.settings;

import java.util.ArrayList;
import java.util.Arrays;

import javafx.scene.paint.Color;

/*
 * Class for representing a game configuration - configurable by game creator
 */
public class GameConfig {
    /*
     * Width and Height of the board
     */
    protected int width;
    protected int height;

    /*
     * Colours for players
     */
    protected ArrayList<Color> playerColors;

    protected final static ArrayList<Color> defaultColours = new ArrayList<Color>(
        Arrays.asList(Color.BLUE, Color.RED, Color.GREEN, Color.GOLD, Color.DARKMAGENTA, Color.CYAN)
    );

    /***
     * Create a game configuration with specified width and height. Default colours will be used
     * @param width - number of cells in a grid per row
     * @param height - number of cells in a grid per column
     */
    public GameConfig(int width, int height){
        this.width = width;
        this.height = height;

        playerColors = defaultColours;
    }
    
    /***
     * Create a game configuration with specified width, height and player colors
     * @param width - number of cells in a grid per row
     * @param height - number of cells in a grid per column
     * @param colors - colors of brushes for players. E.g., color[0] - player 1
     */
    public GameConfig(int width, int height, ArrayList<Color> colors){
        this.width = width;
        this.height = height;

        playerColors = colors;
    }

    public int getWidth(){
        return width;
    }

    public int getHeight() {
        return height;
    }

    public ArrayList<Color> getPlayerColors(){
        return playerColors;
    }
}