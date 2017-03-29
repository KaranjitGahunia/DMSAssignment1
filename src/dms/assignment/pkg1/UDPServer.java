/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dms.assignment.pkg1;

import java.io.IOException;
import static java.lang.Thread.sleep;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 *
 * @author Alex
 */
public class UDPServer extends Thread {

    ArrayList<Connection> connections;

    public UDPServer(ArrayList connections) {
        this.connections = connections;
    }

    public String printConnections() {
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
                        System.out.println(serverResponse);
                    } else {
                        sleep(3000);
                }

            }
        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO: " + e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("Socket: " + e.getMessage());
        } finally {
            if (aSocket != null) {
                aSocket.close();
            }
        }
    }

}
