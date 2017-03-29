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
    String message;
    Connection receiver;
    
    public MessageTo(Connection receiver, String message) {
        this.receiver = receiver;
        this.message = message;
    }
    
}
