package settings;

import java.util.ArrayList;

import exceptions.ColorAlreadyInUseException;
import javafx.scene.paint.Color;

/**
 * Class for representing a game configuration that can be modified by a user
 * Extension of {@link GameConfig GameConfig}
 */
public class EditableGameConfig extends GameConfig{
    /***
     * Create a game configuration with specified width and height. Default colours will be used
     * @param width - number of cells in a grid per row
     * @param height - number of cells in a grid per column
     */
    public EditableGameConfig(int width, int height){
        super(width, height);
    }
    
    /***
     * Create a game configuration with specified width, height and player colors
     * @param width - number of cells in a grid per row
     * @param height - number of cells in a grid per column
     * @param colors - colors of brushes for players. E.g., color[0] - player 1
     */
    public EditableGameConfig(int width, int height, ArrayList<Color> colors){
        super(width, height, colors);
    }

    /**
     * set number of cells per row on the grid
     * @param width
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * set number of cells per column on the grid
     * @param height
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Change the brush color for the specified polayer
     * @param playerId - Player number (starts from 0)
     * @param color - Color to change the brush to
     * 
     * @throws IndexOutOfBoundsException playerId is not a legal player number
     */
    public void changePlayerColor(int playerId, Color color){
        if(playerId < this.playerColors.size() || playerId < 0) throw new IndexOutOfBoundsException();

        playerColors.set(playerId, color);
    }
    
    /**
     * Change the colors for all players
     * @param colors - Color to change the brush to
     */
    public void changeColors(ArrayList<Color> colors){
        this.playerColors = colors;
    }

    /**
     * Add a new player with a color
     * @param color - Color for the new player
     * @throws
     */
    public void addPlayer(Color color) throws ColorAlreadyInUseException{
        if(playerColors.contains(color)){
            throw new ColorAlreadyInUseException();
        }
        playerColors.add(color);
    }
}