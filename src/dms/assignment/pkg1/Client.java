/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dms.assignment.pkg1;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author Alex
 */
public class Client extends JFrame implements ActionListener {

    /**
     * @param args the command line arguments
     */
    final int PORT = 8765;
    public static final String HOST_NAME = "localhost";
    final String DONE = "done";
    String clientRequest;
    DataOutputStream output;
    DataInputStream input;
    Socket socket;
    UDPClient UDPclient;

    public JPanel panel;
    public JPanel textPanel;
    public JScrollPane scrollpane;
    public JTextArea text;
    public JPanel inputPanel;
    public JTextField inputField;
    public JButton confirm;

    public Client() {
        setPreferredSize(new Dimension(500, 400));

        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setResizable(true);

        panel = new JPanel(new BorderLayout());
        add(panel);

        textPanel = new JPanel();
        panel.add(textPanel, BorderLayout.PAGE_START);

        text = new JTextArea();
        text.setEditable(false);
        scrollpane = new JScrollPane(text);
        scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        panel.add(scrollpane);

        inputPanel = new JPanel();
        panel.add(inputPanel, BorderLayout.PAGE_END);
        inputField = new JTextField(25);
        inputPanel.add(inputField);
        confirm = new JButton("Enter");
        confirm.addActionListener(this);
        this.getRootPane().setDefaultButton(confirm);
        inputPanel.add(confirm);

        this.pack();
        this.setVisible(true);
    }

    public void startClient() {
        try {
            socket = new Socket(HOST_NAME, PORT);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        try {
            // setting output and input streams
            output = new DataOutputStream(socket.getOutputStream());
            input = new DataInputStream(socket.getInputStream());
            String serverResponse = null;
            while (true) {
                // get client's name and send to server
                String clientName = (String) JOptionPane.showInputDialog("Please enter your name");
                output.writeUTF(clientName);

                // read server's response.
                // if server's rejects name, notify client and repeat.
                // if server accepts name, proceed.
                serverResponse = input.readUTF();
                if (serverResponse.equals("INVALID NAME. ALREADY IN USE")) {
                    JOptionPane.showMessageDialog(rootPane, serverResponse, "Invalid Input", JOptionPane.ERROR_MESSAGE);
                } else {
                    break;
                }
            }

            System.out.print(serverResponse);
            text.append(serverResponse);

            text.append("Enter message or " + DONE + " to exit client." + "\n");
            UDPclient = new UDPClient(text);
            UDPclient.start();
        } catch (Exception e) {
            System.err.println("Exception occurred: (starting client) " + e.getMessage());
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == confirm) {
            clientRequest = inputField.getText();
            if (DONE.equalsIgnoreCase(clientRequest.trim())) {
                text.setText("Connection closed. Please exit the Client.");
                try {
                    if (input != null) {
                        input.close();
                    }
                    if (output != null) {
                        output.close();
                    }
                    if (socket != null) {
                        socket.close();
                    }
                } catch (Exception exception) {
                    System.err.println(exception.getMessage());
                }
            } else {
                try {
                    output.writeUTF(clientRequest);
                    String serverResponse = input.readUTF();
                    System.out.print(serverResponse);
                    text.append(serverResponse);
                } catch (Exception exception) {
                    System.err.println(exception.getMessage());
                }
            }
            inputField.setText("");
        }
    }

    @Override
    public void dispose() {
        clientRequest = "done";
        try {
            output.writeUTF(clientRequest);
            UDPclient.stopClient();
        } catch (Exception exception) {
            System.err.println(exception.getMessage());
        }
        try {
            if (input != null) {
                input.close();
            }
            if (output != null) {
                output.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (Exception exception) {
            System.err.println("Exception occurred: (dispose) " + exception.getMessage());
        }

        super.dispose();
        System.exit(0);
    }

    public static void main(String args[]) {
        Client client = new Client();

        client.startClient();
    }
}
