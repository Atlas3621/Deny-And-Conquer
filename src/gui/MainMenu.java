package gui;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

import game.settings.GameConfig;
import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

// TODO: Look at generalizing these numbers later

/**
 * One client for the Deny-And-Conquer Game
 */
public class MainMenu extends Application {

    public boolean isValidAddress(TextField IPTF) {
        try {
            InetAddress.getByName(IPTF.getText());
        } catch (UnknownHostException e) {
            return false;
        }
        return true;
    }

    @Override
    public void start(Stage stage) throws Exception {

        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/resources/Menu_UI.fxml")));
        Scene mainScene = new Scene(root);

        stage.setTitle("Deny And Conquer");
        stage.setScene(mainScene);
        stage.show();

        Node NewButton = mainScene.lookup("#NG");
        assert(NewButton instanceof Button);
        Button NB = (Button) NewButton;

        NB.setOnAction(mouseEvent -> {
            try {
                GameSetup gameSetupView = new GameSetup();
                gameSetupView.start(stage);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        Node JoinButton = mainScene.lookup("#JG");
        assert(JoinButton instanceof Button);
        Button JB = (Button) JoinButton;

        JB.addEventHandler(ActionEvent.ACTION, actionEvent -> {
            JB.setText("Connecting...");

            Task<Boolean> task = new Task<>() {
                @Override
                protected Boolean call() {
                    Node IPField = mainScene.lookup("#JGTA");
                    assert (IPField instanceof TextField);
                    TextField IPTF = (TextField) IPField;

                    return isValidAddress(IPTF);
                }
            };

            task.setOnSucceeded(e -> {
                if (!task.getValue()) {
                    // ref: https://www.tutorialspoint.com/how-to-create-an-alert-in-javafx
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Invalid Input Error");
                    alert.setContentText("You have entered an invalid server address. No host is known by this name");
                    alert.showAndWait();
                }

                JB.setText("Join Game");
                task.cancel();

                if (task.getValue()) {
                    Node IPField = mainScene.lookup("#JGTA");
                    assert (IPField instanceof TextField);
                    TextField IPTF = (TextField) IPField;

                    try {
                        InetAddress address = InetAddress.getByName(IPTF.getCharacters().toString());
                        Game myGUI = new Game(address, new GameConfig(5, 5));
                        myGUI.start(stage);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }
                }
            });

            new Thread(task).start();
        });
    }

    public static void main(String[] args) {
        launch(args);
    }

}