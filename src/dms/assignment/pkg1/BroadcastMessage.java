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
public class BroadcastMessage extends Message{
    
    public BroadcastMessage(String message, String sender){
        this.message = message;
        this.sender = sender;
    }
}

