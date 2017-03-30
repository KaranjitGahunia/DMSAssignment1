package dms.assignment.pkg1;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
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
 * Class the represents the client side of the application.
 *
 * @author Alex, Karanjit
 */
public class Client extends JFrame implements ActionListener, ListSelectionListener {

    private final int PORT = 8765;
    private static final String HOST_NAME = "localhost";
    private final String DONE = "done";
    private String clientRequest;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Socket socket;
    private UDPClient UDPclient;
    private ServerListener listener;
    private String receiver;
    private String clientName;

    private JPanel panel;
    private JSplitPane textPanel;
    private JScrollPane scrollpane;
    private JTextArea text;
    private JPanel inputPanel;
    private JTextField inputField;
    private JButton confirm;
    private JList clientList;

    /**
     * default constructor for Client class. initializes the client objects.
     *
     * @param name
     */
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

    /**
     * Method that runs the Client services. Initializes client connection
     * elements of this class. Sets the clients name with the server and then
     * sends messages to other clients that are connected to the server. This
     * process repeats until the server is stopped.
     */
    private void startClient() {
        try {
            socket = new Socket(HOST_NAME, PORT);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            // setting output and input streams
            String serverResponse;
            while (true) {
                clientName = (String) JOptionPane.showInputDialog("Please enter your name");
                out.writeObject(clientName);
                serverResponse = (String) in.readObject();
                if (serverResponse.equalsIgnoreCase("INVALID NAME. ALREADY IN USE".trim())) {
                    JOptionPane.showMessageDialog(rootPane, "Name already in use or contains spaces.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                } else {
                    break;
                }
            }
            text.append("Enter message or " + DONE + " to exit client." + "\n");
            UDPclient = new UDPClient(text, this);
            UDPclient.start();
            listener = new ServerListener();
            listener.start();
        } catch (HeadlessException | IOException | ClassNotFoundException e) {
            System.err.println("Exception occurred: (starting client) " + e.getMessage());
        }
    }

    /**
     * Method used to send a MESSAGETO type message. Creates a message of the
     * appropriate type and sends it to the appropriate client.
     *
     * @param receiver
     * @param message
     */
    private void sendMessageTo(String receiver, String message) {
        try {
            Message messageTo = new Message(message, MessageType.MESSAGETO, receiver);
            out.writeObject(messageTo);
        } catch (IOException exception) {
            System.err.println("Exception occurred in sendMessageTo(): " + exception.getMessage());
        }
    }

    /**
     * Method used to send a BROADCAST type message. Creates a message of the
     * appropriate type and sends it to all clients.
     *
     * @param message
     */
    private synchronized void broadcastMessage(String message) {
        try {
            Message broadcast = new Message(message, MessageType.BROADCAST);
            out.writeObject(broadcast);
        } catch (IOException exception) {
            System.err.println("Exception occurred in broadcastMessage(): " + exception.getMessage());
        }
    }

    /**
     * Method used to send a DISCONNECT type message. Creates a message of the
     * appropriate type and sends it to the appropriate client.
     */
    private void disconnectMessage() {
        try {
            Message dc = new Message("Test", MessageType.DISCONNECT);
            out.writeObject(dc);
        } catch (IOException exception) {
            System.err.println("Exception occurred in disconnectMessage(): " + exception.getMessage());
        }
    }

    /**
     * Calls disconnectMessage method and closes all input/output related
     * variables.
     */
    private void disconnect() {
        disconnectMessage();
        System.out.println("DC MESSAGE SENT");
        text.setText("Connection closed. Please exit the Client.\n");
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (socket != null) {
                socket.close();
            }
            if (UDPclient != null) {
                UDPclient.stopClient();
            }
            if (listener != null) {
                listener.stopListener();
            }
        } catch (IOException exception) {
            System.err.println(exception.getMessage());
        }
    }

    /**
     * Override of the dispose method. Calls disconnect method.
     */
    @Override
    public void dispose() {
        disconnect();

        super.dispose();
        System.exit(0);
    }

    /**
     * Updates the clientList GUI element with the currently connected clients
     * with the server. Also, attempts to reinstate the user's previous
     * selection if possible. Otherwise, default to All.
     *
     * @param clients
     */
    public void updateClientList(DefaultListModel clients) {
        String selection = (String) clientList.getSelectedValue();
        clientList.setModel(clients);

        this.revalidate();
        this.repaint();
        if (clients.contains(selection)) {
            clientList.setSelectedValue(selection, true);
        } else {
            clientList.setSelectedIndex(0);
        }
    }

    /**
     * Handles actions on the GUI.
     *
     * @param e
     */
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

    /**
     * Handles actions on the GUI clientList
     *
     * @param e
     */
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            receiver = String.valueOf(clientList.getSelectedValue());
        }
    }

    public String getClientName(){
        return this.clientName;
    }
    
    /**
     * Main method for client class. Creates a client thread and starts it.
     *
     * @param args
     */
    public static void main(String args[]) {
        Client client = new Client("Client");
        client.startClient();
    }

    /**
     * Private thread class used to receive TCP input from the Server.
     */
    private class ServerListener extends Thread {

        boolean run;

        /**
         * Sets boolean run to false. Used to terminate thread.
         */
        public void stopListener() {
            this.run = false;
        }

        /**
         * Run method for thread. Repeatedly accepts input from Server.
         * Terminates when boolean run is false.
         */
        @Override
        public void run() {
            run = true;
            while (run) {
                try {
                    String message = (String) in.readObject();
                    text.append(message + "\n");
                } catch (IOException ex) {
                    System.out.println("Connection has been closed.");
                    break;
                } catch (ClassNotFoundException ex) {
                    System.out.println(ex);
                }
            }
        }
    }

}
