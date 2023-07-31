package game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import gui.CounterCanvas;
import gui.Results;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.image.PixelWriter;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import tokens.ClearToken;
import tokens.DrawToken;
import tokens.FillToken;
import tokens.TieToken;
import tokens.WinnerToken;

/**
 * An extension of the thread class used in the GUI/Client class to help with TCP
 */
public class Client extends Thread{
    private Socket socket = null;
    private ArrayList<CounterCanvas> rectList = null;

    /**
     * @param list The list of CounterCanvas's representing the initial grid
     * @param socket The client socket from the GUI
     */
    public Client(ArrayList<CounterCanvas> list, Socket socket) {
        super("ClientThread");
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
                    // A cleaner way of handling tokens (we can hide away the parsing in the class, avoiding code reuse)
                    DrawToken fromInput = new DrawToken(inStr);
                    PixelWriter squareWriter = rectList.get(fromInput.getSquareNum()).getGraphicsContext2D().getPixelWriter();
                    squareWriter.setColor(fromInput.getxCord(), fromInput.getyCord(), fromInput.getDrawColor());
                }

                // An if statement to handle the "FILL" tokens
                if (inStr.startsWith("FILL")) {
                    // Parsing Input w/ New Way
                    FillToken fromInput = new FillToken(inStr);
                    // Filling the grid entry (client-side)
                    CounterCanvas targetCanvas = rectList.get(fromInput.getSquareNum());
                    targetCanvas.getGraphicsContext2D().setFill(fromInput.getDrawColor());

                //       targetCanvas.getGraphicsContext2D().fillRect(2, 2, 26, 26);

                    targetCanvas.filled = true;
                }

                if (inStr.startsWith("CLEAR")) {
                    // Parsing Input w/ New Way
                    ClearToken fromInput = new ClearToken(inStr);
                    // Clearing the grid entry (client-side)
                   CounterCanvas targetCanvas = rectList.get(fromInput.getSquareNum());
              //      targetCanvas.resetCanvas();
                    targetCanvas.getGraphicsContext2D().fillRect(2, 2, 26, 26);
                }

                if (inStr.startsWith("TIE")) {
                    
                    //split string into the different colours
                    String colours[] = inStr.split(" ");
          
                    try {
                        Results resultTie = new Results(false, Color.web(colours[1]), Color.web(colours[2]), Color.web(colours[3]), Color.web(colours[4]));
                        resultTie.showResults();
                    } catch (Exception e) {
                        // TODO: handle exception
                    }
                }

                if (inStr.startsWith("WINNER")) {
                    // // Parsing Input w/ New Way
                    
                    WinnerToken fromInput = new WinnerToken(inStr);
                    

                    try {
                        Results resultWin = new Results(true, fromInput.getDrawColor(), fromInput.getDrawColor(), fromInput.getDrawColor(), fromInput.getDrawColor());
                        resultWin.showResults();
                    } catch (Exception e) {
                        // TODO: handle exception
                    }    
                    

                    // Debugging output, can be removed later.
                    System.out.println("CLIENT: The color " + fromInput.getDrawColor() + " has won the game of deny and conquer!");

                    // Debugging to close server
                    out.println("goodbye");
                    System.out.println("CLIENT: sent goodbye to server");
                       
                }

                // Disconnection string, as of yet unused
                if (inStr.equals("goodbye")) {
                    System.out.println("CLIENT: Accepting Goodbye");
                    break;
                }
            }
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
