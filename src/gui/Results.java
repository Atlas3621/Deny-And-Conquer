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

    //take in results whether there was a win and the colours that won or draw
    public Results(Boolean winner, Color c1, Color c2, Color c3, Color c4){
        win = winner;
        color1 = c1;
        color2 = c2;
        color3 = c3;
        color4 = c4;
    }

//  used website below to find how to start stage without run() or start(Stage stage) so method could be called from client.java
//  ref: https://stackoverflow.com/questions/34873673/load-fxml-as-background-process-javafx
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

            Text endMessage = (Text) scene.lookup("#win_message");

            //makes all 4 canvases the same colour if there is a single winner
            if(win == true){

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
            }
            //if there is a tie
            else{
                endMessage.setText("There is a Tie");

                Canvas topLeft = (Canvas) scene.lookup("#colour1");
                Canvas topRight = (Canvas) scene.lookup("#colour2");
                Canvas bottomLeft = (Canvas) scene.lookup("#colour3");
                Canvas bottomRight = (Canvas) scene.lookup("#colour4");

                topLeft.getGraphicsContext2D().setFill(color1);
                topLeft.getGraphicsContext2D().fillRect(0,0,126,128);
                topRight.getGraphicsContext2D().setFill(color2);
                topRight.getGraphicsContext2D().fillRect(0,0,126,128);
                bottomLeft.getGraphicsContext2D().setFill(color3);
                bottomLeft.getGraphicsContext2D().fillRect(0,0,126,128);
                bottomRight.getGraphicsContext2D().setFill(color4);
                bottomRight.getGraphicsContext2D().fillRect(0,0,126,128); 
                
                System.out.println("colour 1 is " + color1);
                System.out.println("colour 1 is " + color2);
                System.out.println("colour 1 is " + color3);
                System.out.println("colour 1 is " + color4);
            }
            //button to end game and leave
            Button quit = (Button) scene.lookup("#quit_game");
            quit.setOnAction((ActionEvent event) -> {
                Platform.exit();
            });

            stage.setScene(scene);
            stage.show();
        });

        loadResult.setOnFailed(e -> loadResult.getException().printStackTrace());

        Thread thread = new Thread(loadResult);
        thread.start();
    }
}
