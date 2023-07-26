package gui;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import game.Client;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import tokens.DrawToken;
import tokens.LiftToken;

// TODO: Look at generalizing these numbers later

/**
 * The other client for the Deny-And-Conquer Game
 */
public class Game extends Application {
    private InetAddress ipAddress;

    public Game (InetAddress ipAddress) {
        this.ipAddress = ipAddress;
    }

    @Override
    public void start(Stage stage) throws Exception {

        // Creating our grid for deny and conquer
        ArrayList<CounterCanvas> gridList = new ArrayList<CounterCanvas>();
        float startPosX = 89.0f;
        float startPosY = 148.0f;

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                // The Spacing Stuff
                CounterCanvas canvasToAdd = new CounterCanvas(30, 30);
                canvasToAdd.setTranslateX(startPosX + (48.0f * i));
                canvasToAdd.setTranslateY(startPosY + (48.0f * j));

                // Setting a line width and a stroke color
                GraphicsContext context = canvasToAdd.getGraphicsContext2D();
                context.setStroke(Color.BLACK);
                context.setLineWidth(3);

                // Drawing our border
                canvasToAdd.drawBorder();

                // Adding to our grid list
                gridList.add(canvasToAdd);
            }
        }

        // Adding our grid to a Group
        Group rectGrid = new Group();
        gridList.forEach(x -> rectGrid.getChildren().add(x));

        // Start Client Side TCP Setup
        Socket MySocket = new Socket(ipAddress, 7070);

        OutputStream os = MySocket.getOutputStream();
        InputStream is = MySocket.getInputStream();

        PrintWriter out = new PrintWriter(os, true);
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        // End Client Side TCP Setup

        // Reading in the colour that the server has provided us with
        String OurColour = in.readLine();
        Color colourToUse = Color.web(OurColour);

        // Starting up a client-side thread to help with TCP receiving
        Client t = new Client(gridList, MySocket);
        t.start();

        // Create our Title Text
        Text titleText = new Text(22, 82, "Deny and Conquer");
        titleText.setWrappingWidth(356);
        titleText.setTextAlignment(TextAlignment.CENTER);
        titleText.setUnderline(true);
        titleText.setFont(new Font(30));
        rectGrid.getChildren().add(titleText);

        // Creating our Scene
        Scene scene = new Scene(rectGrid, 400, 600);
        stage.setTitle("Deny and Conquer: Player View");

        AtomicInteger lastSquare = new AtomicInteger();
        // Handling a mouse click
        scene.setOnMouseDragged(mouseEvent -> {
            if (mouseEvent.getTarget() instanceof CounterCanvas targetCanvas) {
                // We want to figure out the *inner* region of the canvas (i.e. no border)
                // Our stroke width is 3, so if we have 3 on each side this means it will be from x = 3 to 27 non-inclusive, same with y
                double topLeftX = targetCanvas.getTranslateX();
                double topLeftY = targetCanvas.getTranslateY();

                // Creating our so-called "bounds"
                double leftBound = topLeftX + 3;
                double rightBound = topLeftX + 27;

                double topBound = topLeftY + 3;
                double bottomBound = topLeftY + 27;

                // Checking if we are inside the bounds or not
                boolean isInsideX = mouseEvent.getSceneX() >= leftBound && mouseEvent.getSceneX() <= rightBound;
                boolean isInsideY = mouseEvent.getSceneY() >= topBound && mouseEvent.getSceneY() <= bottomBound;

                if (isInsideX && isInsideY && !targetCanvas.filled) {
                    // Reference: https://stackoverflow.com/questions/28417623/the-fastest-filling-one-pixel-in-javafx

                    // We now want to figure out where we actually pressed in our canvas
                    int pressedX = (int) mouseEvent.getX() - (int) topLeftX;
                    int pressedY = (int) mouseEvent.getY() - (int) topLeftY;

                    // Let us make it easier than requiring pixel drawing and give a brush size of 2.
                    // We will originate the brush at the top left
                    int brushSize = 2;

                    for (int i = 0; i < brushSize; i++) {
                        for (int j = 0; j < brushSize; j++) {

                            // Border Collision Checking (Appears to work)
                            boolean pixelIsInsideX = (pressedX + i) <= 26 && (pressedX + i) >= 2;
                            boolean pixelIsInsideY = (pressedY + i) <= 26 && (pressedY + i) >= 2;

                            // If we do not collide with the border, draw the pixel
                            if (pixelIsInsideX && pixelIsInsideY) {
                                // We can use this formula to figure out what number square we are looking for
                                int squareNum = (int) (((targetCanvas.getTranslateX() - startPosX) / 48 * 5) + (targetCanvas.getTranslateY() - startPosY) / 48);
                                lastSquare.set(squareNum);
                                // Here we create our DRAW token and send it across with TCP. The server handles the rest.
                                DrawToken token = new DrawToken(colourToUse, squareNum, (pressedX + i), (pressedY + j));
                                out.println(token);
                            }

                        }
                    }

                }
            }
        });


        // Handling Mouse Lifting
        scene.setOnMouseReleased(mouseDragEvent -> {
            int squareExited = lastSquare.get();
            LiftToken newToken = new LiftToken(colourToUse, squareExited);
            out.println(newToken);
        });

        // Setting up the window and making it non-resizeable
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

}