package dms.assignment.pkg1;

import java.io.IOException;
import static java.lang.Thread.sleep;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JTextArea;

/**
 * The UDPClient class is a thread that is created by the Client class. A
 * UDPClient thread is created for each Client class. It is used to receive and
 * handle UDP data sent by the Server which contains a list of currently
 * connected lists.
 *
 * @author Alex
 */
public class UDPClient extends Thread {

    private JTextArea text;
    private DefaultListModel<String> clients;
    private Client client;
    private boolean run;
    private String hostName;
    
    public UDPClient() {
    }
    /**
     * Default constructor for this class. Initializes the class variables.
     *
     * @param text
     * @param client
     * @param hostName
     */
    public UDPClient(JTextArea text, Client client, String hostName) {
        this.text = text;
        this.client = client;
        this.run = true;
        this.hostName = hostName;
    }

    /**
     * Method to set boolean run to false. This is used to stop the infinite
     * loop in the run method, therefore terminating the thread.
     */
    public void stopClient() {
        this.run = false;
    }

    /**
     * Run method for the UDPClient thread.
     * The thread will try to receive UDP messages from the Server that contain
     * the list of currently connected clients.
     * This list is then processed and send to be updated by the Client class.
     * This occurs every 3 seconds and repeats until the stopClient method is 
     * called.
     */
    public void run() {
        DatagramSocket aSocket = null;
        try {
            while (run) {
                sleep(3000);
                aSocket = new DatagramSocket();
                byte[] buffer = new byte[1000];
                DatagramPacket request = new DatagramPacket("".getBytes(),
                        "".length(), InetAddress.getByName(hostName), 8765);
                aSocket.send(request);
                DatagramPacket serverMessage = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(serverMessage);
                clients = new DefaultListModel<>();
                String[] array = new String(serverMessage.getData()).trim().split(" ");
                for (String s : array) {
                    if (!s.equalsIgnoreCase("null") && !s.equalsIgnoreCase(client.getClientName())) {
                        clients.addElement(s);
                    }
                }
                client.updateClientList(clients);
            }
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException | InterruptedException e) {
            System.out.println("Socket: " + e.getMessage());
        } finally {
            if (aSocket != null) {
                aSocket.close();
            }
        }
    }
}
