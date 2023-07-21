import javafx.application.Application;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.awt.event.MouseEvent;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Objects;

import static javafx.application.Platform.runLater;

// TODO: Look at generalizing these numbers later

/**
 * One client for the Deny-And-Conquer Game
 */
public class GUI extends Application {

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
        Scene mainScene = new Scene(root, 300, 275);

        stage.setTitle("Deny And Conquer");
        stage.setScene(mainScene);
        stage.show();

        Node NewButton = mainScene.lookup("#NG");
        assert(NewButton instanceof Button);
        Button NB = (Button) NewButton;

        NB.setOnAction(mouseEvent -> {
            try {

                Server myServer = new Server();
                Thread serverThread = new Thread(myServer);

                serverThread.start();

                ClientGUI myGUI = new ClientGUI(myServer.address);
                myGUI.start(stage);

            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        Node JoinButton = mainScene.lookup("#JG");
        assert(JoinButton instanceof Button);
        Button JB = (Button) JoinButton;

        JB.addEventHandler(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                JB.setText("Connecting...");

                Task<Boolean> task = new Task<Boolean>() {
                    @Override
                    protected Boolean call() throws Exception {
                        Node IPField = mainScene.lookup("#JGTA");
                        assert (IPField instanceof TextField);
                        TextField IPTF = (TextField) IPField;

                        return isValidAddress(IPTF);
                    }
                };

                task.setOnSucceeded(e -> {
                    if (!task.getValue()) {
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
                            ClientGUI myGUI = new ClientGUI(address);
                            myGUI.start(stage);
                        } catch (Exception ex) {
                            throw new RuntimeException(ex);
                        }
                    }
                });

                new Thread(task).start();
            }

        });
    }

    public static void main(String[] args) {
        launch(args);
    }

}
