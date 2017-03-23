/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dms.assignment.pkg1;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
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
    JTextArea text;

    public Connection(Socket socket, JTextArea text) {
        try {
            clientSocket = socket;
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
            this.text = text;
            this.start();
        } catch (Exception e) {

        }
    }

    public void run() {
        try {
            String clientRequest;
            do {
                clientRequest = in.readUTF() + "\n";
                String serverResponse = "From " + clientSocket.getInetAddress() + ": " + clientRequest;
                System.out.println(serverResponse);
                text.append(serverResponse);
                for (Connection connection : Server.connections) {
                    connection.out.writeUTF(serverResponse);
                }

            } while (clientRequest != null && !DONE.equalsIgnoreCase(clientRequest.trim()));

            System.out.println("Closing Connection with " + clientSocket.getInetAddress());
        } catch (Exception e) {

        }
    }
}
