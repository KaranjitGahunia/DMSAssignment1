/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dms.assignment.pkg1;

import static dms.assignment.pkg1.Server.connections;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import javax.swing.JTextArea;

/**
 *
 * @author Alex
 */
public class UDPServer extends Thread{
//    JTextArea text;
//    
//    public UDPServer(JTextArea text) {
//        this.text = text;
//    }
    
    public void run() {
        DatagramSocket aSocket = null;
        try{
            sleep(10000);
            aSocket = new DatagramSocket(8765);
            byte[] buffer = new byte[100];
            while(true){
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                aSocket.receive(request);               
                
                String serverResponse = "Client List: " + Server.printConnections();
                DatagramPacket serverMessage = new DatagramPacket(serverResponse.getBytes(),
                        serverResponse.length(), request.getAddress(), request.getPort());
                aSocket.send(serverMessage);
            }
        }
        catch (SocketException e){System.out.println("Socket: " + e.getMessage());}
        catch (IOException e) {System.out.println("IO: " + e.getMessage());} 
        catch (InterruptedException e) { System.out.println("Socket: " + e.getMessage());}
        finally {if(aSocket != null) aSocket.close();}
    }
    
    
}
