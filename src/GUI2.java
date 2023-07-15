import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.PixelWriter;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

// TODO: Look at generalizing these numbers later

/**
 * The other client for the Deny-And-Conquer Game
 */
public class GUI2 extends Application {

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
        Socket MySocket = new Socket("127.0.0.1", 7070);

        OutputStream os = MySocket.getOutputStream();
        InputStream is = MySocket.getInputStream();

        PrintWriter out = new PrintWriter(os, true);
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        // End Client Side TCP Setup

        // Reading in the colour that the server has provided us with
        String OurColour = in.readLine();
        Color colourToUse = Color.web(OurColour);

        // Starting up a client-side thread to help with TCP receiving
        DrawClientThread t = new DrawClientThread(gridList, MySocket);
        t.start();

        // Create our Title Text
        Text titleText = new Text(22, 82, "Deny and Conquer");
        titleText.setWrappingWidth(356);
        titleText.setTextAlignment(TextAlignment.CENTER);
        titleText.setUnderline(true);
        titleText.setFont(new Font(30));
        rectGrid.getChildren().add(titleText);

        // Reset Button for the Demo
        // TODO: This isn't integrated with TCP. Something to work on.
        Button resetButton = new Button();
        resetButton.setText("Reset the Canvas'");
        resetButton.setTranslateX(125.0);
        resetButton.setTranslateY(420.0);
        resetButton.setPrefWidth(151.0);
        resetButton.setPrefHeight(29.0);


        resetButton.setOnAction(actionEvent -> gridList.forEach(counterCanvas -> {
            // Reference: https://stackoverflow.com/questions/27203671/javafx-how-to-clear-the-canvas
            GraphicsContext context = counterCanvas.getGraphicsContext2D();
            context.clearRect(0, 0, counterCanvas.getWidth(), counterCanvas.getHeight());
            counterCanvas.resetCtr();

            // Draw border again
            counterCanvas.drawBorder();
        }));

        rectGrid.getChildren().add(resetButton);

        // Creating our Scene
        Scene scene = new Scene(rectGrid, 400, 600);
        stage.setTitle("Deny and Conquer: Player 2");

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

                    PixelWriter canvasWriter = targetCanvas.getGraphicsContext2D().getPixelWriter();

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

                            // TODO: A problem here is that we don't check if the pixel was already filled. Should we?
                            // If we do not collide with the border, draw the pixel
                            if (pixelIsInsideX && pixelIsInsideY) {
                                canvasWriter.setColor((pressedX + i), (pressedY + j), colourToUse);

                                // We can use this formula to figure out what number square we are looking for
                                int squareNum = (int) (((targetCanvas.getTranslateX() - startPosX) / 48 * 5) + (targetCanvas.getTranslateY() - startPosY) / 48);

                                // Here we create our DRAW token and send it across with TCP
                                DrawToken token = new DrawToken(colourToUse, squareNum, (pressedX + i), (pressedY + j));
                                out.println(token.toString());

                                // Here we increment the canvas counter on our side to help us see if we have filled >50%
                                targetCanvas.incrementCtr(1);
                            }

                        }
                    }

                }

                // Calculate a canvas area, based on our fill-able area
                int twiceLineWidth = (int) (targetCanvas.getGraphicsContext2D().getLineWidth() * 2);
                int canvasArea = ((int) targetCanvas.getWidth() - twiceLineWidth) * ((int) targetCanvas.getHeight() - twiceLineWidth);

                // Once the # of pixels drawn is > half the canvas area, fill the square
                if (targetCanvas.getCtr() > 0.5 * canvasArea) {
                    // Send a message to the server that this is now "claimed" by the client
                    int squareNum = (int) (((targetCanvas.getTranslateX() - startPosX) / 48 * 5) + (targetCanvas.getTranslateY() - startPosY) / 48);
                    FillToken token = new FillToken(colourToUse, squareNum);
                    out.println(token.toString());

                    // Actually fill out the canvas with our server-provided color and mark the canvas as filled
                    targetCanvas.getGraphicsContext2D().setFill(colourToUse);
                    // TODO: Determining Proper Values for Here (v, v1, v2, v3)
                    targetCanvas.getGraphicsContext2D().fillRect(2, 2, 26, 26);
                    targetCanvas.filled = true;
                }

            }
        });

        // Setting up the window and making it non-resizeable
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}