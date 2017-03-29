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
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author Alex
 */
public class Client extends JFrame implements ActionListener, ListSelectionListener {

    /**
     * @param args the command line arguments
     */
    final int PORT = 8765;
    public static final String HOST_NAME = "localhost";
    final String DONE = "done";
    String clientRequest;
    DataOutputStream output;
    DataInputStream input;
    ObjectOutputStream out;
    ObjectInputStream in;
    Socket socket;
    UDPClient UDPclient;
    String receiver;
    String clientName;

    public JPanel panel;
    public JSplitPane textPanel;
    public JScrollPane scrollpane;
    public JTextArea text;
    public JPanel inputPanel;
    public JTextField inputField;
    public JButton confirm;
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

        clientList = new JList();
        clientList.addListSelectionListener(this);

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
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            // setting output and input streams
            String serverResponse = "";
            while (true) {
                // get client's name and send to server
                clientName = (String) JOptionPane.showInputDialog("Please enter your name");
                out.writeObject(clientName);
                // read server's response.
                // if server's rejects name, notify client and repeat.
                // if server accepts name, proceed.
                serverResponse = (String) in.readObject();
                if (serverResponse.equalsIgnoreCase("INVALID NAME. ALREADY IN USE".trim())) {
                    JOptionPane.showMessageDialog(rootPane, serverResponse, "Invalid Input", JOptionPane.ERROR_MESSAGE);
                } else {
                    break;
                }
            }
            
            
            
            text.append("Enter message or " + DONE + " to exit client." + "\n");
            
            UDPclient = new UDPClient(text, this);
            UDPclient.start();
            new ServerListener().start();
            
        } catch (Exception e) {
            System.err.println("Exception occurred: (starting client) " + e.getMessage());
        }
    }

    public void sendMessageTo(String receiver, String message) {
        try {
            Message messageTo = new Message(message, MessageType.MESSAGETO, receiver);
            out.writeObject(messageTo);
        } catch (Exception exception) {
            System.err.println("Exception occurred in sendMessageTo(): " + exception.getMessage());
        }
    }

    public void broadcastMessage(String message) {
        try {
            Message broadcast = new Message(message, MessageType.BROADCAST);
            out.writeObject(broadcast);
        } catch (Exception exception) {
            System.err.println("Exception occurred in broadcastMessage(): " + exception.getMessage());
        }
    }

    public void disconnectMessage() {
        try {
            Message dc = new Message("Test", MessageType.DISCONNECT);
            out.writeObject(dc);
        } catch (Exception exception) {
            System.err.println("Exception occurred in disconnectMessage(): " + exception.getMessage());
        }
    }

    public void updateClientList(DefaultListModel clients) {
        String selection = (String) clientList.getSelectedValue();
        System.out.println("Selection: " + selection);
        clientList.setModel(clients);
        
        this.revalidate();
        this.repaint();
        clientList.setSelectedValue(selection, true);
    }

    public void disconnect() {
        disconnectMessage();
        System.out.println("DC MESSAGE SENT");
        if (UDPclient != null) {
            UDPclient.stopClient();
        }
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
                    if (receiver != null) {
                        if (!receiver.equalsIgnoreCase("ALL")) {
                            sendMessageTo(receiver, clientRequest);
                        } else {
                            broadcastMessage(clientRequest);
                        }
                    } else {
                        broadcastMessage(clientRequest);
                    }

                } catch (Exception exception) {
                    System.err.println("Exception occurred in actionPerformed(): " + exception.getMessage());
                }
            }
            inputField.setText("");
        }
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            receiver = String.valueOf(clientList.getSelectedValue());
            System.out.println(receiver);
        }
    }

    @Override
    public void dispose() {
        disconnect();

        super.dispose();
        System.exit(0);
    }

    public static void main(String args[]) {
        Client client = new Client("Client");

        client.startClient();
    }

    class ServerListener extends Thread {

        public void run() {
            while (true) {
                try {
                    String message = (String) in.readObject();
                    text.append(message + "\n");
                } catch (IOException ex) {
                    System.out.println("Connection has been closed.");
                    System.err.println(ex);
                    break;
                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

}
