import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * A Class that Extends Canvas from JavaFX with an Integrated Counter
 */
public class CounterCanvas extends Canvas {
    private int counter;
    /**
     * Whether or not the Canvas is filled (i.e. Conquered)
     */
    public boolean filled;

    /**
     * @param i Canvas Width
     * @param i1 Canvas Height
     */
    public CounterCanvas(int i, int i1) {
        super(i, i1);

        this.filled = false;
        this.counter = 0;
    }

    /**
     * Gets the value of the integrated Canvas Counter
     * @return Value of Canvas Counter (Ideally # of Pixels)
     */
    public int getCtr() {
        return this.counter;
    }

    /**
     * Increments the counter by some provided amount
     * @param incrSize How much to increment counter
     */
    public void incrementCtr(int incrSize) {
        this.counter += incrSize;
    }

    /**
     * Reset counter to 0 and reset the filled boolean
     */
    public void resetCtr() {
        this.counter = 0;
        this.filled = false;
    }


    /**
     * Simple macro to draw a border around each square
     */
    public void drawBorder() {
        GraphicsContext context = this.getGraphicsContext2D();
        context.strokeRect(0, 0, 30, 30);
    }
}
