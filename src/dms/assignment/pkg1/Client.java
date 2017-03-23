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
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author Alex
 */
public class Client extends JPanel implements ActionListener {

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

    static JFrame frame;
    public JPanel panel;
    public JPanel textPanel;
    public JScrollPane scrollpane;
    public JTextArea text;
    public JPanel inputPanel;
    public JTextField inputField;
    public JButton confirm;

    public Client() {
        super(new BorderLayout());
        setPreferredSize(new Dimension(500, 400));

        frame = new JFrame("Client");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.getContentPane().add(this);

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
        frame.getRootPane().setDefaultButton(confirm);
        inputPanel.add(confirm);

        frame.pack();
        frame.setVisible(true);
    }

    public void startClient() {
        Scanner keyboard = new Scanner(System.in);

        try {
            socket = new Socket(HOST_NAME, PORT);
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        try {
            output = new DataOutputStream(socket.getOutputStream());
            input = new DataInputStream(socket.getInputStream());
            System.out.println("Enter message or " + DONE + " to exit client.");
            text.append("Enter message or " + DONE + " to exit client." + "\n");

            do {
                clientRequest = keyboard.nextLine();
                output.writeUTF(clientRequest);
//                String serverResponse = input.readUTF();
//                System.out.println("Server Response: " + serverResponse);
//                text.append("Server Response: " + serverResponse + "\n");
            } while (!DONE.equalsIgnoreCase(clientRequest.trim()));
        } catch (Exception e) {
            System.err.println(e.getMessage());
        } finally {
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
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == confirm) {
            clientRequest = inputField.getText();
            if (DONE.equalsIgnoreCase(clientRequest.trim())) {
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

    public static void main(String args[]) {
        Client client = new Client();

        client.startClient();
    }
}
