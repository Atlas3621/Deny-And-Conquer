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
//    String color;

//    private String[] colours = {"Blue", "Red", "Green", "Yellow", "Dark Magenta", "Cyan"};
    
    public Results(Boolean winner, Color c1, Color c2, Color c3, Color c4){
        win = winner;
        color1 = c1;
        color2 = c2;
        color3 = c3;
        color4 = c4;

        // if (winner == true){
        //     System.out.println("Blue value is " + c1.getBlue());
        //     System.out.println("Blue value is " + c1.getRed());
        //     System.out.println("Blue value is " + c1.getGreen());
        //     if(c1.getBlue() == 1.0 && c1.getRed() == 0.0 && c1.getGreen() == 0.0){
        //         color = "Blue";
        //     }
        //     else if (c1.getBlue() == 0.0 && c1.getRed() == 1.0 && c1.getGreen() == 0.0){
        //         color = "Red";
        //     }
        //     else if (c1.getBlue() == 0.0 && c1.getRed() == 0.0 && c1.getGreen() == 1.0){
        //         color = "Green";
        //     }
        // }
        // System.out.println("color is " + color);
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
//  https://stackoverflow.com/questions/34873673/load-fxml-as-background-process-javafx
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
//           stage.initOwner();
//            Text endMessage = (Text) scene.lookup("#win_message");
            if(win == true){
//                endMessage.setText("Winner is");

                Canvas topLeft = (Canvas) scene.lookup("#colour1");
                Canvas topRight = (Canvas) scene.lookup("#colour2");
                Canvas bottomLeft = (Canvas) scene.lookup("#colour3");
                Canvas bottomRight = (Canvas) scene.lookup("#colour4");
                topLeft.getGraphicsContext2D().setFill(color1);
                topLeft.getGraphicsContext2D().fillRect(0,0,126,128);
                topRight.getGraphicsContext2D().setFill(color1);
                topRight.getGraphicsContext2D().fillRect(0,0,126,128);
                bottomLeft.getGraphicsContext2D().setFill(color1);
                bottomLeft.getGraphicsContext2D().fillRect(0,0,126,128);
                bottomRight.getGraphicsContext2D().setFill(color1);
                bottomRight.getGraphicsContext2D().fillRect(0,0,126,128);

                Button quit = (Button) scene.lookup("#quit_game");
                quit.setOnAction((ActionEvent event) -> {
                    Platform.exit();
                });
            }
            stage.setScene(scene);
            stage.show();
        });

        loadResult.setOnFailed(e -> loadResult.getException().printStackTrace());

        Thread thread = new Thread(loadResult);
        thread.start();
    }
}
