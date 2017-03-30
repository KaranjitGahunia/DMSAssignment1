package dms.assignment.pkg1;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * Class that represents the server side of the application. Contains a list of
 * connections with clients.
 *
 * @author Alex
 */
public class Server extends JPanel {

    final int PORT = 8765;
    public static List<Connection> connections;
    public JTextArea text;
    UDPServer UDPserver;
    private JScrollPane scrollpane;

    /**
     * Default constructor. Initializes the GUI elements.
     */
    public Server() {
        super(new BorderLayout());
        setPreferredSize(new Dimension(500, 400));
        text = new JTextArea();
        scrollpane = new JScrollPane(text);
        scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollpane);
    }

    /**
     * Method that runs the server activities. Initializes server elements of
     * this Class. Creates Connection threads with new clients. Connections are
     * started and stored in a list. This process repeats until the server is
     * stopped.
     */
    public void startServer() {
        ServerSocket serverSocket = null;
        boolean stopServer = false;
        try {
            connections = Collections.synchronizedList(new ArrayList<>());
            serverSocket = new ServerSocket(PORT);
            System.out.println("Server started at " + InetAddress.getLocalHost() + " on port " + PORT);
            text.append("Server started at " + InetAddress.getLocalHost() + " on port " + PORT + "\n");
            while (!stopServer) {
                Socket socket = serverSocket.accept();
                System.out.println("Connection made with " + socket.getInetAddress());
                text.append("Connection made with " + socket.getInetAddress() + "\n");
                Connection connection = new Connection(socket, text);
                connections.add(connection);
                if (UDPserver == null) {
                    UDPserver = new UDPServer(connections);
                    UDPserver.start();
                }
            }
            serverSocket.close();
        } catch (IOException e) {
            stopServer = true;
            System.err.println(e.getMessage());
        }
    }

    /**
     * Main method for Server class.
     * Sets up the JFrame and calls startServer method.
     * @param args 
     */
    public static void main(String args[]) {
        Server server = new Server();

        JFrame frame;
        frame = new JFrame("Server");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.getContentPane().add(server);
        frame.setLocationRelativeTo(null);
        Point p = frame.getLocation();
        p.x = p.x - 250;
        p.y = p.y - 200;
        frame.setLocation(p);
        frame.pack();
        frame.setVisible(true);

        server.startServer();
    }
}
