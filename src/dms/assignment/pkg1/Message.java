/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dms.assignment.pkg1;

import java.io.Serializable;

/**
 *
 * @author Alex
 */
public class Message implements Serializable {

    private String message;
    private String receiver;
    private MessageType type;

    public Message(String message, MessageType type) {
        this.message = message;
        this.type = type;
    }

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
