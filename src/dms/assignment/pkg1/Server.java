/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dms.assignment.pkg1;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 *
 * @author Alex
 */
public class Server extends JPanel {

    final int PORT = 8765;
    public static ArrayList<Connection> connections;
    public JTextArea text;
    private JScrollPane scrollpane;

    public Server() {
        super(new BorderLayout());
        setPreferredSize(new Dimension(500, 400));

        text = new JTextArea();
        scrollpane = new JScrollPane(text);
        scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollpane);

    }

    public void startServer() {
        ServerSocket serverSocket = null;
        boolean stopServer = false;

        try {
            connections = new ArrayList<>();
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started at " + InetAddress.getLocalHost() + " on port " + PORT);
            text.append("Server started at " + InetAddress.getLocalHost() + " on port " + PORT + "\n");
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        while (!stopServer) {
            try {
                Socket socket = serverSocket.accept();
                System.out.println("Connection made with " + socket.getInetAddress());
                text.append("Connection made with " + socket.getInetAddress() + "\n");
                Connection connection = new Connection(socket, text);
                connections.add(connection);
                
                sendServerMessage();
            } catch (Exception e) {
                stopServer = true;
                System.err.println(e.getMessage());
            }
        }
        try {
            serverSocket.close();
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
    
    public void sendServerMessage() {
        DatagramSocket aSocket = null;
        try{
            aSocket = new DatagramSocket(8765);
            byte[] buffer = new byte[100];
            while(true){
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(request);               
                
                System.out.println("SENDING MESSAGE");
                
                String serverResponse = "Client List: " + connections.toString();
                DatagramPacket serverMessage = new DatagramPacket(serverResponse.getBytes(),
                        serverResponse.length(), request.getAddress(), request.getPort());
                aSocket.send(serverMessage);
            }
        }
        catch (SocketException e){System.out.println("Socket: " + e.getMessage());}
        catch (IOException e) {System.out.println("IO: " + e.getMessage());}
        finally {if(aSocket != null) aSocket.close();}
    }

    public static void main(String args[]) {
        Server server = new Server();

        JFrame frame;
        frame = new JFrame("Server");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.getContentPane().add(server);
        frame.pack();
        frame.setVisible(true);

        server.startServer();
    }
}
