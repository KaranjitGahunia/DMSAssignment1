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
                
                UDPServer UDPserver = new UDPServer();
                UDPserver.start();
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
