import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;

import java.awt.*;
import java.net.*;
import java.io.*;
import java.util.ArrayList;

/**
 * An extension of the thread class used in the GUI/Client class to help with TCP
 */
public class DrawClientThread extends Thread {
    private Socket socket = null;
    private ArrayList<CounterCanvas> rectList = null;

    /**
     * @param list The list of CounterCanvas's representing the initial grid
     * @param socket The client socket from the GUI
     */
    public DrawClientThread(ArrayList<CounterCanvas> list, Socket socket) {
        super("DrawClientThread");
        this.socket = socket;
        this.rectList = list;
    }

    @Override
    public void run() {

        try {
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader((socket.getInputStream())));

            String inStr, outStr;
            while ((inStr = in.readLine()) != null) {
                // An if statement to handle the "DRAW" tokens
                if (inStr.startsWith("DRAW")) {

                    Color colorToUse = Color.web(inStr.substring(6, 14));
                    int square = Integer.parseInt(inStr.substring(14, 16));
                    int xCord = Integer.parseInt(inStr.substring(16, 18));
                    int yCord = Integer.parseInt(inStr.substring(18, 20));

                    PixelWriter squareWriter = rectList.get(square).getGraphicsContext2D().getPixelWriter();
                    squareWriter.setColor(xCord, yCord, colorToUse);
                }

                // An if statement to handle the "FILL" tokens
                if (inStr.startsWith("FILL")) {
                    Color colorToUse = Color.web(inStr.substring(6, 14));
                    int square = Integer.parseInt(inStr.substring(14, 16));

                    CounterCanvas targetCanvas = rectList.get(square);
                    targetCanvas.getGraphicsContext2D().setFill(colorToUse);
                    targetCanvas.getGraphicsContext2D().fillRect(2, 2, 26, 26);
                    targetCanvas.filled = true;
                }

                // Disconnection string, as of yet unused
                if (inStr.equals("goodbye")) {
                    break;
                }
            }
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
