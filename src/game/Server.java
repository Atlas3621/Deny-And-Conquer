package game;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import game.settings.GameConfig;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import tokens.ClearToken;
import tokens.DrawToken;
import tokens.FillToken;
import tokens.GameConfigToken;
import tokens.LiftToken;
import tokens.WinnerToken;

/**
 * The TCP Server Class for our Deny and Conquer Game
 */
public class Server implements Runnable
{
    /**
     * Help with creating a new server
     */
    public InetAddress address;
    public ServerSocket server;
    static Board gameBoard;
    /**
     * This ArrayList stores all of our valid (connected) clients.
     * It is useful for when we need to broadcast to all of the clients.
     */
    static ArrayList<Pair<Socket, Color>> validClients = new ArrayList<>();
    /**
     * This array stores a preset collection of colors for clients to choose from, and the orders in which they are chosen.
     * Effectively, the length of the array is the maximum # of clients we can have.
     */
    static Color[] colorChoices = { Color.BLUE, Color.RED, Color.GREEN, Color.GOLD };
    static int curChoice = 0;

    /**
     * Configuration for the current game: board size and player colors
     */
    private GameConfig gameConfig;

    /**
     * Chooses a color for a newly connected client, checking if there are any colors left to choose
     * @return A Color for a Client
     */
    public static Color chooseColor() {
        if (curChoice > colorChoices.length) {
            return null;
        }

        Color choice = colorChoices[curChoice];
        curChoice += 1;

        return choice;
    }

    public Server (int canvasSize, int lineWidth, GameConfig config) throws IOException {
        server = new ServerSocket(7070);
        address = InetAddress.getByName("localhost");

        colorChoices = config.getPlayerColors().toArray(colorChoices);

        this.gameConfig = config;
        gameBoard = new Board(canvasSize, config.getWidth(), config.getHeight(), lineWidth);
    }

    @Override
    public void run() {
        // Resources that I have used. We use the Ref3 technique for a PoC here.
        // Ref: https://www.baeldung.com/a-guide-to-java-sockets
        // Ref2: https://stackoverflow.com/questions/26789754/handling-multiple-tcp-connections-in-java-server-side
        // Ref3: https://docs.oracle.com/javase/tutorial/networking/sockets/clientServer.html
        // Ref4: https://cs.lmu.edu/~ray/notes/javanetexamples/

        // Our loop here for connecting to TCP Clients
        while (true) {
            try {
                // We accept a client and choose its color
                Socket clientSocket = this.server.accept();
                Color drawing = chooseColor();

                // If we cannot find a color, we just disconnect
                if (drawing == null) {
                    // TODO: Add some pretty disconnection logic here.
                    clientSocket.close();
                }

                // Some debugging help here to log which clients connect and with which colors (their hex values)
                System.out.println("ACCEPTED: " + clientSocket + " WITH COLOUR " + drawing);

                // Send the newly connected client its color
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                GameConfigToken gameConfigToken = new GameConfigToken(gameConfig);

                out.println(drawing);
                out.println(gameConfigToken);

                // We then add the newly validated client to our list from above, and start a new thread for it.
                validClients.add(new Pair<Socket, Color>(clientSocket, drawing));
                DrawServerThread t = new DrawServerThread(clientSocket);
                t.start();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Our class for threads communicating with clients on the server-side
     */
    private static class DrawServerThread extends Thread {
        private Socket socket = null;

        /**
         * Create a new server-side thread for a specified client socket
         * @param socket the socket of the client for which the thread is being created
         */
        public DrawServerThread(Socket socket) {
            super("DrawServerThread");
            this.socket = socket;
        }

        @Override
        public void run() {

            try {
                // Setup for TCP Connection
                BufferedReader in = new BufferedReader(new InputStreamReader((socket.getInputStream())));
                String inStr, outStr;

                // While loop to continue reading in TCP messages sent to the server from this client
                while ((inStr = in.readLine()) != null) {

                    // Create the message and store it in the finalOutStr variable
                    outStr = inStr;
                    String finalOutStr = outStr;

                    if (outStr.startsWith("DRAW")) {
                        DrawToken fromInput = new DrawToken(outStr);

                        Color colorToUse = fromInput.getDrawColor();
                        int square = fromInput.getSquareNum();
                        int xCord = fromInput.getxCord();
                        int yCord = fromInput.getyCord();

                        if (gameBoard.isAvailableToDraw(colorToUse, square)) {

                            gameBoard.setClaimed(colorToUse, square);
                            gameBoard.drawPixel(square, colorToUse);

                            validClients.forEach(s -> {
                                try {

                                    PrintWriter cOut = new PrintWriter(s.getKey().getOutputStream(), true);
                                    cOut.println(new DrawToken(colorToUse, square, xCord, yCord));

                                    if (gameBoard.checkFilled(colorToUse, square)) {
                                        cOut.println(new FillToken(colorToUse, square));
                                        gameBoard.setFilled(colorToUse, square);
                                        int numOfPlayers = validClients.size();
                                        if (gameBoard.winnerExists(numOfPlayers)) { //game is over
                                            if (gameBoard.isATie()){ //is it a tie?
                                                System.out.println("isATie is true");
                                                cOut.println("TIE"); //send this "token" to clients
                                            }
                                            else { //not a tie, we have a signle winner, do the usual logic
                                                System.out.println("WE HAVE A WINNER");
                                                //cOut.println("TIE");
                                                cOut.println(new WinnerToken(gameBoard.colorOfWinner())); 
                                            }
                                            //cOut.println("goodbye");
                                        }
                                    }

                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            });

                        }

                    } else if (outStr.startsWith("LIFT")) {
                        LiftToken fromInput = new LiftToken(outStr);
                        int squareToClear = fromInput.getSquareNum();

                        if (gameBoard.isAvailableToDraw(fromInput.getDrawColor(), squareToClear)) {
                            ClearToken newToken = new ClearToken(squareToClear);
                            gameBoard.resetPiece(squareToClear);

                            validClients.forEach(s -> {
                                try {
                                    PrintWriter cOut = new PrintWriter(s.getKey().getOutputStream(), true);
                                    cOut.println(newToken);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            });

                        }

                    } else {
                        // Broadcast our message to all clients that are not us (the current client).
                            validClients.forEach(s -> {
                                if (!s.getKey().equals(socket)) {
                                    try {
                                        PrintWriter cOut = new PrintWriter(s.getKey().getOutputStream(), true);
                                        cOut.println(finalOutStr);
                                    } catch (IOException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            });
                    }

                    // Disconnection string, as of yet unused
                    if (outStr.equals("goodbye")) {
                        System.out.println("SERVER: Accepted Goodbye and Sending Goodbye to Clients");
                        validClients.forEach(s -> {
                            try {
                                PrintWriter cOut = new PrintWriter(s.getKey().getOutputStream(), true);
                                cOut.println("goodbye");
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        });
                        break;
                    }
                }
                socket.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
    }

}
