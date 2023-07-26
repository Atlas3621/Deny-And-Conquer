package gui;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

/**
 * A Class that Extends Canvas from JavaFX with an Integrated Counter
 */
public class CounterCanvas extends Canvas {
    /**
     * Whether or not the Canvas is filled (i.e. Conquered) on the client-side
     */
    public boolean filled;

    /**
     * @param i Canvas Width
     * @param i1 Canvas Height
     */
    public CounterCanvas(int i, int i1) {
        super(i, i1);
    }

    /**
     * A simple helper function to just reset our canvas to its original state and draw a border around it
     */
    public void resetCanvas() {
        this.filled = false;
        GraphicsContext context = this.getGraphicsContext2D();
        context.clearRect(0, 0, this.getWidth(), this.getHeight());
        context.strokeRect(0, 0, 30, 30);
    }

    /**
     * A simple helper function to just draw a border around each square.
     */
    public void drawBorder() {
        GraphicsContext context = this.getGraphicsContext2D();
        context.strokeRect(0, 0, 30, 30);
    }
}
