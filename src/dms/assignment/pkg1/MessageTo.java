/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dms.assignment.pkg1;

/**
 *
 * @author Alex
 */
public class MessageTo extends Message{
    
    private String receiver;
    
    public MessageTo(String sender, String message, String receiver){
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
    }
    
    public String getReceiver(){
        return receiver;
    }
}
