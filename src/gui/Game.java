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
import game.settings.GameConfig;
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
import tokens.GameConfigToken;
import tokens.LiftToken;

// TODO: Look at generalizing these numbers later

/**
 * The other client for the Deny-And-Conquer Game
 */
public class Game extends Application {
    private InetAddress ipAddress;
    private GameConfig gameConfig;

    public Game (InetAddress ipAddress, GameConfig gameConfig) {
        this.ipAddress = ipAddress;
        this.gameConfig = gameConfig;
    }

    @Override
    public void start(Stage stage) throws Exception {

        // Start Client Side TCP Setup
        Socket MySocket = new Socket(ipAddress, 7070);

        OutputStream os = MySocket.getOutputStream();
        InputStream is = MySocket.getInputStream();

        PrintWriter out = new PrintWriter(os, true);
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        // End Client Side TCP Setup 
        
        // client receives two messages from the server before game begins:
        // 1) Color of the current player - might change to # of the player later
        // 2)  Game configuration: board size and colors of all players
        // TCP guarantees that messages come in order so we don't need to worry about that 

        // 1) Reading in the colour that the server has provided us with
        String OurColour = in.readLine();
        Color playerColor = Color.web(OurColour);
        // 2) Reading the game config
        GameConfigToken gcToken = new GameConfigToken(in.readLine());
        GameConfig gameConfig = gcToken.getGameConfig();
        

         // Creating our grid for deny and conquer
        ArrayList<CounterCanvas> gridList = new ArrayList<CounterCanvas>();

        int canvasWidth = 30, canvasHeight = 30;

        // Adding our grid to a Group
        Group rectGrid = new Group();
        // Create our Title Text
        Text titleText = new Text(22, 82, "Deny and Conquer");
        titleText.setWrappingWidth(356);
        titleText.setTextAlignment(TextAlignment.CENTER);
        titleText.setUnderline(true);
        titleText.setFont(new Font(30));
        rectGrid.getChildren().add(titleText);

        // calculate the offset so that the grid is centered horizontally
        // (window size - grid size) / 2.0
        double offsetX = (rectGrid.getBoundsInLocal().getWidth() +60 - 48 * gameConfig.getWidth()) / 2.0;
        final double startPosX = offsetX;
        final double startPosY = 148.0;

        System.out.println("offsetX: " + offsetX);
        System.out.println("grid row width: " + 48 * gameConfig.getWidth());
        
        for (int i = 0; i < gameConfig.getWidth(); i++) {
            for (int j = 0; j < gameConfig.getHeight(); j++) {
                // The Spacing Stuff
                CounterCanvas canvasToAdd = new CounterCanvas(canvasWidth, canvasHeight);
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

        gridList.forEach(x -> rectGrid.getChildren().add(x));

        // Starting up a client-side thread to help with TCP receiving
        Client t = new Client(gridList, MySocket);
        t.start();

        
        System.out.println(rectGrid.getBoundsInLocal().getWidth());
        // adjust window bounds based on the grid size
        double windowWidth = rectGrid.getBoundsInLocal().getWidth() + 40;
        double windowHeight = rectGrid.getBoundsInLocal().getHeight() + titleText.getBoundsInLocal().getHeight() + 60;

        System.out.println("width: " + windowWidth);
        System.out.println("height: " +windowHeight);
        // Creating our Scene
        Scene scene = new Scene(rectGrid, windowWidth, windowHeight);
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
                                int squareNum = (int) (((targetCanvas.getTranslateX() - startPosX) / 48 * gameConfig.getHeight()) + (targetCanvas.getTranslateY() - startPosY) / 48);
                                lastSquare.set(squareNum);
                                // Here we create our DRAW token and send it across with TCP. The server handles the rest.
                                DrawToken token = new DrawToken(playerColor, squareNum, (pressedX + i), (pressedY + j));
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
            LiftToken newToken = new LiftToken(playerColor, squareExited);
            out.println(newToken);
        });

        // Setting up the window and making it non-resizeable
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

}