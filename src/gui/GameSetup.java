package gui;

import java.util.Objects;

import game.Server;
import game.settings.EditableGameConfig;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.stage.Stage;

/**
 * Window for configuring the game and waiting for other players
 */
public class GameSetup extends Application {
    private EditableGameConfig gameConfig;

    @Override
    public void start(Stage stage) throws Exception {

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/resources/GameSetup_UI.fxml")));
        Scene mainScene = new Scene(root);

        stage.setTitle("Deny And Conquer");
        stage.setScene(mainScene);

        this.gameConfig = new EditableGameConfig(5, 5);

        // configuring width selector. Default value is 5
        ChoiceBox<Integer> widthSelector = (ChoiceBox<Integer>) mainScene.lookup("#Width-Selector");
        widthSelector.getItems().addAll(3,4,5,6,7,8);
        widthSelector.setValue(5);
        widthSelector.valueProperty().addListener((obs, old, w) -> {
            gameConfig.setWidth(w);
        });
        
        // configuring height selector. Default value is 5
        ChoiceBox<Integer> heightSelector = (ChoiceBox<Integer>) mainScene.lookup("#Height-Selector");
        heightSelector.setValue(5);
        heightSelector.getItems().addAll(3,4,5,6,7,8);
        heightSelector.valueProperty().addListener((obs, old, h) -> {
            gameConfig.setHeight(h);
        });

        
        // color selectors. For now 4 players is max, maybe will make dynamic later
        for(int i = 0; i < 4; ++i) {
            ColorPicker colorPicker = (ColorPicker) mainScene.lookup("#Player-"+(i+1)+"-Color-Selector");
            colorPicker.setValue(gameConfig.getColorForPlayer(i));
            
            // final copy of i, that can be used inside the listener below
            final int playerIndex = i;
            colorPicker.valueProperty().addListener((obs, old, c) -> {
                gameConfig.changePlayerColor(playerIndex, c);
            });
        }


        // launch game server and start the game after start game was pressed
        Button startGame = (Button) mainScene.lookup("#Start-Game");
        startGame.setOnAction(mouseEvent -> {
            try {
                Server myServer = new Server(30, 3, gameConfig);
                Thread serverThread = new Thread(myServer);

                serverThread.start();

                Game myGUI = new Game(myServer.address, gameConfig);
                myGUI.start(stage);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        // leave GameSetup and go back to main menu if exit was pressed
        Button leaveGameSetup = (Button) mainScene.lookup("#Leave-GameSetup");
        leaveGameSetup.setOnAction(mouseEvent -> {
            try {
                // go back to main menu
                (new MainMenu()).start(stage);
            } catch(Exception e) {
                throw new RuntimeException(e);
            }
        });

        stage.show();
    }
}