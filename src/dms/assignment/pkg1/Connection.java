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
import java.util.Iterator;
import javax.swing.JTextArea;

/**
 *
 * @author Alex
 */
public class Connection extends Thread {

    final String DONE = "done";
    DataInputStream in;
    DataOutputStream out;
    Socket clientSocket;
    Message message;
    JTextArea text;
    ArrayList<Connection> connections;
    String clientName;

    public Connection(Socket socket, JTextArea text, ArrayList connections) {
        try {
            clientSocket = socket;
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
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
            clientName = null;

            while (clientRequest != null && !DONE.equalsIgnoreCase(clientRequest.trim())) {
                //message = (Message) in.readObject();
                String serverResponse = "From " + clientSocket.getInetAddress() + ": ";

                clientRequest = in.readUTF();

                if (clientName == null) {
                    if (uniqueName(clientRequest)) {
                        serverResponse = "Set " + clientSocket.getInetAddress() + " client name to " + clientRequest + "\n";
                        clientName = clientRequest;
                    } else {
                        serverResponse = "INVALID NAME. ALREADY IN USE \n";
                    }

                } else {
                    serverResponse = "From " + clientName + "[" + clientSocket.getInetAddress() + "]: " + clientRequest + "\n";
                }

                text.append(serverResponse);

                System.out.println(serverResponse);
                for (Connection connection : connections) {
                    connection.out.writeUTF(serverResponse);
                }

//                if (message instanceof MessageTo) {
//                    Connection receiver = message.getReceiver();
//                    receiver.out.writeUTF(serverResponse);
//                } else if (message instanceof BroadcastMessage) {
//                    for (Connection connection : connections) {
//                        connection.out.writeUTF(serverResponse);
//                    }
//                } else if (message instanceof DisconnectMessage) {
//
//                }
            }

            System.out.println("Closing Connection with " + clientSocket.getInetAddress());
            text.append("Closing Connection with " + clientSocket.getInetAddress());
            connections.remove(this);

        } catch (Exception e) {

        }
    }

    private boolean uniqueName(String name) {
        if (Server.connections.isEmpty()) {
            System.out.println("Name is unique");
            return true;
        }
        for (Connection connection : connections) {
            System.out.println("Comparing: " + connection.clientName + " " + name);
            if (!connection.equals(this)) {
                if (connection.clientName.equalsIgnoreCase(name)) {
                    System.out.println("Name isn't unique");
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
