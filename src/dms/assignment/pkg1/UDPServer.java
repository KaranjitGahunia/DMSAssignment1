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

/**
 *
 * @author Alex
 */
public class UDPServer extends Thread{
    ArrayList<Connection> connections;
    
    public UDPServer(ArrayList connections) {
        this.connections = connections;
    }
    
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

