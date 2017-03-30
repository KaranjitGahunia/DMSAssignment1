package dms.assignment.pkg1;

import java.io.IOException;
import static java.lang.Thread.sleep;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Iterator;
import java.util.List;

/**
 * The UDPServer class is a thread that is created by the Server class. A
 * UDPServer thread is created for each Server class. It is used to send UDP
 * data to all currently connected Clients which contains a list of currently
 * connected lists.
 *
 * @author Alex
 */
public class UDPServer extends Thread {

    private List<Connection> connections;

    /**
     * Default constructor for this class. Initializes the connections list.
     *
     * @param connections
     */
    public UDPServer(List connections) {
        this.connections = connections;
    }

    /**
     * Method that returns a String representation of the connections list.
     *
     * @return
     */
    private String printConnections() {
        String output = "";
        if (connections != null) {
            Iterator<Connection> connectionsIterator = connections.iterator();
            Connection current;
            while (connectionsIterator.hasNext()) {
                current = connectionsIterator.next();
                output += current.toString() + " ";
            }
        }
        return output;
    }

    /**
     * Run method for UDPServer threads. The thread will try to send UDP
     * messages that contain the list of currently connected clients to all
     * currently connected clients. This occurs every 3 seconds and repeats.
     */
    public void run() {
        DatagramSocket aSocket = null;
        try {
            aSocket = new DatagramSocket(8765);
            byte[] buffer = new byte[100];
            while (true) {
                if (connections != null) {
                    DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                    aSocket.receive(request);
                    String serverResponse = "ALL " + printConnections();
                    DatagramPacket serverMessage = new DatagramPacket(serverResponse.getBytes(),
                            serverResponse.length(), request.getAddress(), request.getPort());
                    aSocket.send(serverMessage);
                } else {
                    sleep(3000);
                }
            }
        } catch (SocketException | InterruptedException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        } finally {
            if (aSocket != null) {
                aSocket.close();
            }
        }
    }
}
