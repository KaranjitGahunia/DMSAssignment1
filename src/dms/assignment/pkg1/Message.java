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
public abstract class Message {
    protected String message;
    protected String sender;
    
    public String getMessage(){
        return message;
    }
    
    public String getSender(){
        return sender;
    }
}
