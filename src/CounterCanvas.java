import javafx.scene.canvas.Canvas;

public class CounterCanvas extends Canvas {
    private int counter;
    public boolean filled;
    public CounterCanvas(int i, int i1) {
        super(i, i1);

        this.filled = false;
        this.counter = 0;
    }

    public int getCtr() {
        return this.counter;
    }

    public void incrementCtr(int incrSize) {
        this.counter += incrSize;
    }

    public void resetCtr() {
        this.counter = 0;
        this.filled = false;
    }
}
