/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dms.assignment.pkg1;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.JTextArea;

/**
 *
 * @author Alex
 */
public class Connection extends Thread {

    final String DONE = "done";
    DataInputStream input;
    DataOutputStream output;
    ObjectOutputStream out;
    ObjectInputStream in;
    Socket clientSocket;
    Message message;
    JTextArea text;
    ArrayList<Connection> connections;
    String clientName;

    public Connection(Socket socket, JTextArea text, ArrayList connections) {
        try {
            clientSocket = socket;
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            this.text = text;
            this.connections = connections;
            this.start();
        } catch (Exception e) {

        }
    }

    @Override
    public void run() {
        try {
            String clientRequest = "";
            String serverResponse = "";
            clientName = null;

            if (clientName == null) {
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
                if (in.readObject() instanceof Message) {
                    message = (Message) in.readObject();
                    serverResponse = message.getMessage();

                    switch (message.getType()) {
                        case MESSAGETO:
                            for (Connection connection : connections) {
                                if (connection.clientName.equalsIgnoreCase(message.getReceiver().trim())) {
                                    connection.out.writeObject("From " + clientName + ": " + serverResponse);
                                }
                            }
                            break;
                        case BROADCAST:
                            for (Connection connection : connections) {
                                connection.out.writeObject("From " + clientName + ": " + serverResponse);
                            }
                            break;
                        case DISCONNECT:
                            System.out.println("Closing Connection with " + clientSocket.getInetAddress());
                            String disconnectMsg = "Closing Connection with " + clientSocket.getInetAddress();
                            for (Connection connection : connections) {
                                connection.out.writeUTF(disconnectMsg);
                            }
                            for (Connection connection : connections) {
                                if (connection.clientName.equalsIgnoreCase(clientName)) {
                                    connections.remove(connection);
                                }

                            }
                            break;
                        default:
                            break;
                    }
                }

            }

        } catch (Exception e) {

        }
    }

    private boolean uniqueName(String name) {
        if (connections.isEmpty()) {
            return true;
        }
        for (Connection connection : connections) {
            if (!connection.equals(this)) {
                if (connection.clientName.equalsIgnoreCase(name)) {
                    return false;
                }
            }
        }
        System.out.println("Name is unique");

        return true;
    }

    public String toString() {
        return clientName;
    }
}
