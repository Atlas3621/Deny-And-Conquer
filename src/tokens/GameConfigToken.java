package tokens;

import java.util.ArrayList;

import game.settings.GameConfig;
import javafx.scene.paint.Color;

/**
 * A token to share the game configuration with board size and player colors
 */
public class GameConfigToken{
    private GameConfig gameConfig;

    /**
     * Create a token from a game config
     * @param gameConfig - game configuration to be sent to other players
     */
    public GameConfigToken(GameConfig gameConfig){
        this.gameConfig = gameConfig;
    }

    /**
     * Parses a token string into the GameConfigToken
     * @param tokenString - token string to parse. Format is assumed to be "TOKEN width height color1 color2 ..."
     */
    public GameConfigToken(String tokenString) {
        // splits the tokenString by whitespaces
        String[] data = tokenString.split("\\s+");

        int width = Integer.parseInt(data[1]);
        int height = Integer.parseInt(data[2]);

        ArrayList<Color> colors = new ArrayList<>();

        // the remaining data are player colors
        for(int i = 3; i < data.length; ++i){
            colors.add(Color.web(data[i]));
        }

        this.gameConfig = new GameConfig(width, height, colors);
    }

    /**
     * @return Game configuration coming from the server
     */
    public GameConfig getGameConfig() {
        return this.gameConfig;
    }

    @Override
    public String toString() {
        int width = gameConfig.getWidth();
        int height = gameConfig.getHeight();

        String xWidth = (width < 10? "0":"") + width;
        String xHeight = (width < 10? "0":"") + height;
        
        String xColors = "";
        for(Color color : gameConfig.getPlayerColors()){
            xColors += color + " ";
        }
        return "GAME_CONFIG " + xWidth + " " + xHeight + " " + xColors;
    }
}
