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

import java.util.ArrayList;

public class GUI extends Application {

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

                // Drawing a box around each canvas (the "border")
                GraphicsContext context = canvasToAdd.getGraphicsContext2D();
                context.setStroke(Color.BLACK);
                context.setLineWidth(3);
                context.strokeRect(0, 0, 30, 30);

                // Adding to our grid list
                gridList.add(canvasToAdd);
            }
        }

        Group rectGrid = new Group();
        gridList.forEach(x -> rectGrid.getChildren().add(x));

        // Title Text
        Text titleText = new Text(22, 82, "Deny and Conquer");
        titleText.setWrappingWidth(356);
        titleText.setTextAlignment(TextAlignment.CENTER);
        titleText.setUnderline(true);
        titleText.setFont(new Font(30));
        rectGrid.getChildren().add(titleText);

        // Reset Button for the Demo
        Button resetButton = new Button();
        resetButton.setText("Reset the Canvas'");
        resetButton.setTranslateX(125.0);
        resetButton.setTranslateY(420.0);
        resetButton.setPrefWidth(151.0);
        resetButton.setPrefHeight(29.0);

        resetButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent actionEvent) {
                gridList.forEach(counterCanvas -> {
                    // Reference: https://stackoverflow.com/questions/27203671/javafx-how-to-clear-the-canvas
                    GraphicsContext context = counterCanvas.getGraphicsContext2D();
                    context.clearRect(0, 0, counterCanvas.getWidth(), counterCanvas.getHeight());
                    counterCanvas.resetCtr();

                    // Draw border again
                    context.strokeRect(0, 0, 30, 30);
                });
            }

        });

        rectGrid.getChildren().add(resetButton);

        // Creating our Scene
        Scene scene = new Scene(rectGrid, 400, 600);
        stage.setTitle("Deny and Conquer");


        // Handling a mouse click
        scene.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
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
                        System.out.println("We are targeting a canvas");
                        System.out.println("x: " + targetCanvas.getTranslateX() + " y: " + targetCanvas.getTranslateY());

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
                                canvasWriter.setColor(pressedX + i, pressedY + j, Color.BLACK);
                            }
                        }

                        // Track the # of filled pixels in the canvas:
                        targetCanvas.incrementCtr(4);
                    }

                    int canvasArea = (int) targetCanvas.getWidth() * (int) targetCanvas.getHeight();

                    if (targetCanvas.getCtr() > 0.5 * canvasArea) {
                        targetCanvas.getGraphicsContext2D().fillRect(0, 0, 30, 30);
                        targetCanvas.filled = true;
                    }

                }
            }
        });

        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}