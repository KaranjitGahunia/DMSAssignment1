package dms.assignment.pkg1;

import java.io.Serializable;

/**
 * Class used to hold information regarding messages that are sent between clients.
 * Contains the message, the recipients, and the type of the message.
 * Message objects are sent between clients and the server.
 * @author Alex
 */
public class Message implements Serializable {

    private String message;
    private String receiver;
    private MessageType type;

    /**
     * Constructor for Message. Initializes the message and type variables.
     * This constructor is used for BROADCAST and DISCONNECT type Message objects.
     * @param message
     * @param type 
     */
    public Message(String message, MessageType type) {
        this.message = message;
        this.type = type;
    }

    /**
     * Constructor for Message. Initializes the message, receiver, and type variables.
     * This constructor is used for MESSAGETO type Message objects.
     * @param message
     * @param type
     * @param receiver 
     */
    public Message(String message, MessageType type, String receiver) {
        this.message = message;
        this.type = type;
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }

    public String getReceiver() {
        return receiver;
    }
    
    public MessageType getType() {
        return type;
    }

}
