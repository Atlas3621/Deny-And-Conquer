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

// TODO: Look at generalizing these numbers later
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
                    counterCanvas.drawBorder();
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
                        System.out.println("Mouse Event Y: " + mouseEvent.getY() + "\ntopLeftY: " + topLeftY);
                        int pressedX = (int) mouseEvent.getX() - (int) topLeftX;
                        int pressedY = (int) mouseEvent.getY() - (int) topLeftY;
                        System.out.println(pressedY);

                        // Let us make it easier than requiring pixel drawing and give a brush size of 2.
                        // We will originate the brush at the top left
                        int brushSize = 2;

                        for (int i = 0; i < brushSize; i++) {
                            for (int j = 0; j < brushSize; j++) {

                                // Border Collision Checking (Appears to work)
                                boolean pixelIsInsideX = (pressedX + i) <= 26 && (pressedX + i) >= 2;
                                boolean pixelIsInsideY = (pressedY + i) <= 26 && (pressedY + i) >= 2;

                                // TODO: A problem here is that we don't check if the pixel was already filled
                                // If we do not collide with the border, draw the pixel
                                if (pixelIsInsideX && pixelIsInsideY) {
                                    canvasWriter.setColor((pressedX + i), (pressedY + j), Color.RED);
                                    // Track the # of filled pixels in the canvas:
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
                        targetCanvas.getGraphicsContext2D().setFill(Color.RED);
                        // TODO: Determining Proper Values for Here (v, v1, v2, v3)
                        targetCanvas.getGraphicsContext2D().fillRect(2, 2, 26, 26);
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