/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dms.assignment.pkg1;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import javax.swing.JTextArea;

/**
 *
 * @author Alex
 */
public class UDPClient extends Thread {

    JTextArea text;
    public boolean run;

    public UDPClient(JTextArea text) {
        this.text = text;
        run = true;
    }
    
    public void stopClient() {
        run = false;
    }

    public void run() {
        DatagramSocket aSocket = null;
        try {
            while (run) {
                sleep(10000);
                aSocket = new DatagramSocket();
                byte[] buffer = new byte[100];
                String m = "";
                DatagramPacket request = new DatagramPacket(m.getBytes(),
                        m.length(), InetAddress.getLocalHost(), 8765);
                aSocket.send(request);

                DatagramPacket serverMessage = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(serverMessage);
                text.append(new String(serverMessage.getData()).trim() + "\n");
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
