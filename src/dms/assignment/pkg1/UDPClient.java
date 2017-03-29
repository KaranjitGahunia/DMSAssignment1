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
import java.net.InetAddress;
import java.net.SocketException;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JTextArea;

/**
 *
 * @author Alex
 */
public class UDPClient extends Thread {

    JTextArea text;
    DefaultListModel<String> clients;
    JList clientList;
    Client client;
    public boolean run;

    public UDPClient(JTextArea text, Client client) {
        this.text = text;
        this.client = client;
        run = true;
    }

    public void stopClient() {
        run = false;
    }

    public void run() {
        DatagramSocket aSocket = null;
        try {
            while (run) {
                sleep(3000);
                aSocket = new DatagramSocket();
                byte[] buffer = new byte[1000];
                DatagramPacket request = new DatagramPacket("".getBytes(),
                        "".length(), InetAddress.getLocalHost(), 8765);
                aSocket.send(request);

                DatagramPacket serverMessage = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(serverMessage);
                clients = new DefaultListModel<>();
                
                String[] array = new String(serverMessage.getData()).trim().split(" ");
                
                for (String s : array) {
                    if (!s.equalsIgnoreCase("null") && !s.equalsIgnoreCase(client.clientName)) {
                        
                        clients.addElement(s);
                    }
                    
                }
                
                client.updateClientList(clients);
            }

        } catch (SocketException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("Socket: " + e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("Socket: " + e.getMessage());
        } finally {
            if (aSocket != null) {
                aSocket.close();
            }
        }
    }

}

