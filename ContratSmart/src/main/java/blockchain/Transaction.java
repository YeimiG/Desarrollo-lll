/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package blockchain;

 import java.io.Serializable;
 import java.util.Date;
/**
 *
 * @author owen7
 */
public class Transaction implements Serializable{
    
    private int id;
    private long timeStamp;
    private String sender;
    private String receiver;
    private double amount;

    public Transaction(int pId, String pSender, String pReceiver, double pAmount) {
        this.id = pId;
        this.timeStamp = new Date().getTime();
        this.sender = pSender;
        this.receiver = pReceiver;
        this.amount = pAmount;
    }

    @Override
    public String toString() {
        return "Tx#" + id + " [" + sender + " -> " + receiver + " : $" + amount + "]";
    }

    public int getId() { return id; }
    public long getTimeStamp() { return timeStamp; }
    public String getSender() { return sender; }
    public String getReceiver() { return receiver; }
    public double getAmount() { return amount; }
}
 
