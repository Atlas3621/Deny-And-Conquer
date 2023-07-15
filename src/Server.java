import javafx.scene.paint.Color;

import java.io.*; import java.net.*;
import java.util.ArrayList;
import java.util.Objects;

/**
 * The TCP Server Class for our Deny and Conquer Game
 */
public class Server
{
    /**
     * This ArrayList stores all of our valid (connected) clients.
     * It is useful for when we need to broadcast to all of the clients.
     */
    static ArrayList<Socket> validClients = new ArrayList<>();
    /**
     * This array stores a preset collection of colors for clients to choose from, and the orders in which they are chosen.
     * Effectively, the length of the array is the maximum # of clients we can have.
     */
    static Color[] colorChoices = {Color.BLUE, Color.RED}; static int curChoice = 0;

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

    public static void main(String[] args) throws IOException {

        ServerSocket server = new ServerSocket(7070);

        // Resources that I have used. We use the Ref3 technique for a PoC here.
        // Ref: https://www.baeldung.com/a-guide-to-java-sockets
        // Ref2: https://stackoverflow.com/questions/26789754/handling-multiple-tcp-connections-in-java-server-side
        // Ref3: https://docs.oracle.com/javase/tutorial/networking/sockets/clientServer.html
        // Ref4: https://cs.lmu.edu/~ray/notes/javanetexamples/

        // Our loop here for connecting to TCP Clients
        while (true) {
            try {
                // We accept a client and choose its color
                Socket clientSocket = server.accept();
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
                out.println(drawing);

                // We then add the newly validated client to our list from above, and start a new thread for it.
                validClients.add(clientSocket);
                DrawServerThread t = new DrawServerThread(clientSocket);
                t.start();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Our class for threads for clients on the server-side
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
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader((socket.getInputStream())));
                String inStr, outStr;

                // While loop to continue reading in TCP messages sent to the server from this client
                while ((inStr = in.readLine()) != null) {

                    // Create the message and store it in the finalOutStr variable
                    outStr = inStr;
                    String finalOutStr = outStr;

                    // Broadcast our message to all clients that are not us (the current client).
                    validClients.forEach(c -> {
                        if (!c.equals(socket)) {
                            try {
                                PrintWriter cOut = new PrintWriter(c.getOutputStream(), true);
                                cOut.println(finalOutStr);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    });

                    // Disconnection string, as of yet unused
                    if (outStr.equals("goodbye")) {
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
