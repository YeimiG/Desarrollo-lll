/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package blockchain;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author owen7
 */
public class Block {
    private int id;
    private long timeStamp;
    private String previousHash;
    private String hash;
    private int nonce;
    private List<Transaction> transactions;

    public Block(int id, String previousHash) {
        this.id = id;
        this.previousHash = previousHash;
        this.timeStamp = new Date().getTime();
        this.transactions = new ArrayList<>();
        this.nonce = 0;
        this.hash = generateHash();
    }

    // Calcula el Hash SHA-256 del bloque completo
    public String generateHash() {
        String dataToHash = id + Long.toString(timeStamp) + previousHash + Integer.toString(nonce) + transactions.toString();
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(dataToHash.getBytes("UTF-8"));
            StringBuilder buffer = new StringBuilder();
            for (byte b : bytes) {
                buffer.append(String.format("%02x", b));
            }
            return buffer.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addTransaction(Transaction tx) {
        this.transactions.add(tx);
    }

    public int getId() { return id; }
    public long getTimeStamp() { return timeStamp; }
    public void setTimeStamp(long timeStamp) { this.timeStamp = timeStamp; }
    public String getPreviousHash() { return previousHash; }
    public void setPreviousHash(String previousHash) { this.previousHash = previousHash; }
    public String getHash() { return hash; }
    public void setHash(String hash) { this.hash = hash; }
    public int getNonce() { return nonce; }
    public void setNonce(int nonce) { this.nonce = nonce; }
    public List<Transaction> getTransactions() { return transactions; }

    @Override
    public String toString() {
        return "Bloque #" + id + " [Hash=" + hash + ", PrevHash=" + previousHash + ", Nonce=" + nonce + ", TxCount=" + transactions.size() + "]";
    }
    
}
