package gui;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

import javafx.concurrent.Task;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class Results{
    Boolean win;
    Color color1;
    Color color2;
    Color color3;
    Color color4;
    
    public Results(Boolean draw, Color c1, Color c2, Color c3, Color c4){
        win = draw;
        color1 = c1;
        color2 = c2;
        color3 = c3;
        color4 = c4;
    }

//     @Override
//     public void run(){
//         Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/resources/Results_UI.fxml")));
//         Scene mainScene = new Scene(root);

//         // stage.setTitle("Deny and Conquer Results");
//         // stage.setScene(mainScene);

//         Text endMessage = (Text) mainScene.lookup("#win_message");
//         if(win == true){
//             endMessage.setText("Winner is " + color1 + "!");

//             Canvas topLeft = (Canvas) mainScene.lookup("#colour1");
//             Canvas topRight = (Canvas) mainScene.lookup("#colour2");
//             Canvas bottomLeft = (Canvas) mainScene.lookup("#colour3");
//             Canvas bottomRight = (Canvas) mainScene.lookup("#colour4");
//             topLeft.getGraphicsContext2D().setFill(color1);
//             topRight.getGraphicsContext2D().setFill(color1);
//             bottomLeft.getGraphicsContext2D().setFill(color1);
//             bottomRight.getGraphicsContext2D().setFill(color1);

//             Button quit = (Button) mainScene.lookup("#quit_game");
//             quit.setOnAction((ActionEvent event) -> {
//                 Platform.exit();
//             });
//         }

// //        stage.show();
//    }

    public void showResults(){
        Task<Parent> loadResult = new Task<Parent>(){
            @Override
            public Parent call() throws IOException, InterruptedException{

                FXMLLoader loadResult = new FXMLLoader(getClass().getResource("/resources/Results_UI.fxml"));
                Parent root = loadResult.load();
                return root;
            }
        };
        loadResult.setOnSucceeded(e -> {
            Scene scene = new Scene(loadResult.getValue());
            Stage stage = new Stage();
//            stage.initOwner();
            stage.setScene(scene);
            stage.show();
        });

        loadResult.setOnFailed(e -> loadResult.getException().printStackTrace());

        Thread thread = new Thread(loadResult);
        thread.start();
    }
}
