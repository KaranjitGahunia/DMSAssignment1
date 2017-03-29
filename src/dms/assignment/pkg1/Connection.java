/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dms.assignment.pkg1;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Iterator;
import javax.swing.JTextArea;

/**
 *
 * @author Alex
 */
public class Connection extends Thread {

    final String DONE = "done";
    ObjectInputStream in;
    ObjectOutputStream out;
    Socket clientSocket;
    JTextArea text;
    String clientName;
    Message message;

    public Connection(Socket socket, JTextArea text) {
        try {
            clientSocket = socket;
            in = new ObjectInputStream(clientSocket.getInputStream());
            out = new ObjectOutputStream(clientSocket.getOutputStream());
            this.text = text;
            this.start();
        } catch (Exception e) {

        }
    }

    @Override
    public void run() {
        try {
            String clientRequest = "";

            while (clientRequest != null && !DONE.equalsIgnoreCase(clientRequest.trim())) {
                String serverResponse = null;
                clientRequest = (String) in.readObject();
                if (clientName == null) {
                    if (uniqueName(clientRequest)) {
                        serverResponse = "Set " + clientSocket.getInetAddress() + " client name to " + clientRequest + "\n";
                        clientName = clientRequest;
                    } else {
                        serverResponse = "INVALID NAME. ALREADY IN USE";
                    }
                } else {
                    serverResponse = "From " + clientName + "[" + clientSocket.getInetAddress() + "]: " + clientRequest + "\n";
                }

                if (message instanceof MessageTo) {
                    Connection receiver = message.getReceiver();
                    receiver.out.writeUTF(serverResponse);
                } else if (message instanceof BroadcastMessage) {
                    for (Connection connection : Server.connections) {
                        connection.out.writeUTF(serverResponse);
                    }
                } else if (message instanceof DisconnectMessage) {

                }
            }
            System.out.println("Closing Connection with " + clientSocket.getInetAddress());
            text.append("Closing Connection with " + clientSocket.getInetAddress());
            Server.connections.remove(this);

        } catch (Exception e) {

        }
    }

    private boolean uniqueName(String name) {
//        if (Server.connections.isEmpty()) {
//            System.out.println("Name is unique");
//            return true;
//        }
//        Iterator<Connection> iterator = Server.connections.iterator();
//        while (iterator.hasNext()) {
//            System.out.println("Searching through connections");
//            if (iterator.next().clientName.equalsIgnoreCase(name)) {
//                System.out.println("Name isn't unique");
//                return false;
//            }
//        }
//        System.out.println("Name is unique");
        if (name.equalsIgnoreCase("Karanjit")) {
            return true;
        }
        return false;
    }

    public String toString() {
        return clientName + " [" + clientSocket.getInetAddress() + "]";
    }
}
