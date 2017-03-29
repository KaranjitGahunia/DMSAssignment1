/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dms.assignment.pkg1;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListModel;

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
    DefaultListModel<String> clients;

    public JPanel panel;
    public JSplitPane textPanel;
    public JScrollPane scrollpane;
    public JTextArea text;
    public JPanel inputPanel;
    public JTextField inputField;
    public JButton confirm;
    public JPanel clientPanel;
    public JLabel clientLabel;
    public JList clientList;

    public Client(String name) {
        super(name);
        setPreferredSize(new Dimension(500, 400));

        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.setResizable(true);
        this.setLocationRelativeTo(null);
        Point p = this.getLocation();
        p.x = p.x - 250;
        p.y = p.y - 200;
        this.setLocation(p);

        panel = new JPanel(new BorderLayout());
        add(panel);
        
        textPanel = new JSplitPane();
        panel.add(textPanel);

        text = new JTextArea();
        text.setEditable(false);
        scrollpane = new JScrollPane(text);
        scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        clientPanel = new JPanel();
        clientLabel = new JLabel("Client List: ");
        
        clientList = new JList();
        clientPanel.add(clientLabel, BorderLayout.PAGE_START);
        clientPanel.add(clientList, BorderLayout.PAGE_END);

        textPanel.setLeftComponent(scrollpane);
        textPanel.setRightComponent(clientList);
        textPanel.setResizeWeight(0.8);
        textPanel.setDividerLocation(0.8);

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
            output = new DataOutputStream(socket.getOutputStream());
            input = new DataInputStream(socket.getInputStream());
            // setting output and input streams
            String serverResponse = null;
            while (true) {
                // get client's name and send to server
                String clientName = (String) JOptionPane.showInputDialog("Please enter your name");
                
                output.writeUTF(clientName);

                // read server's response.
                // if server's rejects name, notify client and repeat.
                // if server accepts name, proceed.
                serverResponse = input.readUTF();
                if (serverResponse.equalsIgnoreCase("INVALID NAME. ALREADY IN USE".trim())) {
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

    public void sendMessageTo(Connection receiver, String message) {
        MessageTo messageTo = new MessageTo(receiver, message);
        try {
            //output.writeObject(messageTo);
        } catch (Exception exception) {
            System.err.println(exception.getMessage());
        }
    }

    public void broadcastMessage(String message) {
        BroadcastMessage broadcast = new BroadcastMessage(message);
        try {
            //output.writeObject(broadcast);
        } catch (Exception exception) {
            System.err.println(exception.getMessage());
        }
    }

    public void disconnect() {
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
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == confirm) {
            clientRequest = inputField.getText();
            if (DONE.equalsIgnoreCase(clientRequest.trim())) {
                disconnect();
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
            if (UDPclient != null) {
                UDPclient.stopClient();
            }

        } catch (Exception exception) {
            System.err.println(exception.getMessage());
        }
        disconnect();

        super.dispose();
        System.exit(0);
    }

    public static void main(String args[]) {
        Client client = new Client("Client");

        client.startClient();
    }
}
