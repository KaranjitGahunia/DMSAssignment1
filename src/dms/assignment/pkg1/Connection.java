/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dms.assignment.pkg1;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
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
    JTextArea text;
    String clientName;

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

    @Override
    public void run() {
        try {
            String clientRequest = "";

            while (clientRequest != null && !DONE.equalsIgnoreCase(clientRequest.trim())) {
                String serverResponse = null;
                clientRequest = in.readUTF();
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
                System.out.println(serverResponse);
                text.append(serverResponse);
                for (Connection connection : Server.connections) {
                    connection.out.writeUTF(serverResponse);
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
        if(name.equalsIgnoreCase("Karanjit")){
            return true;
        }
        return false;
    }
    
    public String toString(){
        return clientName + " [" + clientSocket.getInetAddress() + "]";
    }
}
