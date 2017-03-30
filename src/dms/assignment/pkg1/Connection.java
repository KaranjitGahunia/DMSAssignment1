package dms.assignment.pkg1;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.swing.JTextArea;

/**
 * The Connection class is a thread that is created by the Server class. A
 * Connection thread is created for each active client. This class connects each
 * Client with the Server. All messages are transfered using these Connection
 * threads.
 *
 * @author Alex
 */
public class Connection extends Thread {

    final String DONE = "done";
    ObjectOutputStream out;
    ObjectInputStream in;
    Socket clientSocket;
    Message message;
    JTextArea text;
    String clientName;

    /**
     * Default constructor for Connection class. Initializes the socket and text
     * variables and starts the thread.
     *
     * @param socket
     * @param text
     */
    public Connection(Socket socket, JTextArea text) {
        try {
            clientSocket = socket;
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            this.text = text;
            this.start();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    /**
     * Run method for Connection threads Initializes the clientName. Checks if
     * the clientName proposed by client is valid or not. Repeats until client
     * enters a valid name. Receives messages from client and sends the messages
     * to appropriate clients. Threads response to the messages depends on the
     * MessageType. This repeats until client sends a DISCONNECT message, after
     * which the run method completes and the thread terminates.
     *
     *
     */
    @Override
    public void run() {
        try {
            String clientRequest = "";
            String serverResponse = "";
            clientName = null;

            while (clientName == null) {
                clientRequest = (String) in.readObject();
                if (uniqueName(clientRequest)) {
                    serverResponse = "Set " + clientSocket.getInetAddress() + " client name to " + clientRequest + "\n";
                    clientName = clientRequest;
                } else {
                    serverResponse = "INVALID NAME. ALREADY IN USE";
                }
                out.writeObject(serverResponse);
            }

            while (clientRequest != null && !DONE.equalsIgnoreCase(clientRequest.trim())) {
                message = (Message) in.readObject();
                serverResponse = message.getMessage();

                switch (message.getType()) {
                    case MESSAGETO:
                        for (Connection connection : Server.connections) {
                            if (connection.clientName.equalsIgnoreCase(message.getReceiver().trim())) {
                                connection.out.writeObject("[From " + clientName + "]: " + serverResponse);
                            }
                            if (connection.clientName.equalsIgnoreCase(clientName.trim())) {
                                connection.out.writeObject("[To " + message.getReceiver() + "]: " + serverResponse);
                            }
                        }
                        break;
                    case BROADCAST:
                        for (Connection connection : Server.connections) {
                            connection.out.writeObject("[ALL] " + clientName + ": " + serverResponse);
                        }
                        break;
                    case DISCONNECT:
                        System.out.println("Closing Connection with " + clientSocket.getInetAddress());
                        String disconnectMsg = "[" + clientName + " has disconnected]";
                        for (Connection connection : Server.connections) {
                            if (connection != null) {
                                connection.out.writeObject(disconnectMsg);
                            }
                        }
                        for (Connection connection : Server.connections) {
                            if (connection.clientName.equalsIgnoreCase(clientName)) {
                                Server.connections.remove(connection);
                            }
                        }
                        break;
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e);
        }
    }

    /**
     * Checks if the name provided is unique or not. If the name provided
     * already exists in any of the connections in the server, the method will
     * return false. If not, the method will return true.
     * Method is used in the run method for this class.
     * @param name
     * @return
     */
    private boolean uniqueName(String name) {
        if (Server.connections.isEmpty()) {
            return true;
        }
        if (name.matches(".*\\s+.*")) {
            return false;
        }
        for (Connection connection : Server.connections) {
            if (!connection.equals(this)) {
                if (connection.clientName.equalsIgnoreCase(name)) {
                    return false;
                }
            }
        }
        System.out.println("Name is unique");
        return true;
    }

    /**
     * toString method for Connection thread.
     * Returns the clientName value.
     * @return 
     */
    @Override
    public String toString() {
        return clientName;
    }
}
