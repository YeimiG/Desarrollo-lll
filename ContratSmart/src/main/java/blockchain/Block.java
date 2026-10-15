package blockchain;

import java.io.Serializable;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.nio.charset.StandardCharsets;


public class Block implements Serializable {

    private int id;
    private long timeStamp;
    private String previousHash;
    private String hash;
    private int nonce;
    private List<SmartContract> smartContracts;

    public Block(int id, String previousHash) {
        this (id, previousHash, new Date().getTime());   
    }
    public Block(int id, String previousHash, Long timeStamp){
        this.id = id;
        this.previousHash = previousHash;
        this.timeStamp = timeStamp;
        this.smartContracts = new ArrayList<>();
        this.nonce = 0;
        this.hash = generateHash();
    }

    /*
        REconstruye un bloque ya minado  comprobar si el hash corrresponde al contenido 
    */
    public static Block restaurar(int id, long timeStamp, String previousHash, String hash,
                                  int nonce, List<SmartContract> contratos) {
        Block b = new Block(id, previousHash, timeStamp);
        b.nonce = nonce;
        if (contratos != null) {
            b.smartContracts.addAll(contratos);
        }
        b.hash = hash;
        return b;
    }
    
    
    // Calcula el algoritmo criptografico Hash SHA-256 del bloque completo
    public String generateHash() {
       StringBuilder data = new StringBuilder();
         
       //Datos principales del bloque
       appendField (data, Integer.toString(id));
       appendField (data, Long.toString(timeStamp));
       appendField (data, previousHash);
       appendField (data, Integer.toString(nonce));
       
       //Cantidad Contratos
       appendField (data, Integer.toString(smartContracts.size()));
       
       //Datos de cada contrato, orden definido
       for (SmartContract contract : smartContracts){
           appendField(data, contract.getIdContrato());
           appendField(data, contract.getIdEmpleado());
           appendField(data, contract.getNombreEmpleado());
           appendField(data, contract.getPuesto());
           appendField(data, Double.toString(contract.getSalario()));
           appendField(data, contract.getSucursal());         
       }
       try {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        byte[] hashBytes = digest.digest(
            data.toString().getBytes(StandardCharsets.UTF_8)
        );

        StringBuilder hash = new StringBuilder();

        for (byte b : hashBytes) {
            hash.append(String.format("%02x", b & 0xff));
        }

        return hash.toString();

        }catch (Exception e) {
            throw new IllegalStateException(
                "No fue posible calcular el hash del bloque.", e
        );
    }
}

/**
 * Agrega cada campo con su longitud para evitar ambiguedades al concatenar 
 */
private void appendField(StringBuilder data, String value){
    if (value == null){
        data.append("-1:");
    }else{
        data.append(value.length())
            .append(':')
            .append(value);
    }
     /* String dataToHash = id + Long.toString(timeStamp) + previousHash + Integer.toString(nonce) + smartContracts.toString();
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
        }*/
    }

    // Métodos para gestionar los Contratos Inteligentes
    public void addSmartContract(SmartContract contract) {
        this.smartContracts.add(contract);
    }

    public int countSmartContracts() {
        return this.smartContracts.size();
    }

    public SmartContract getSmartContract(int index) {
        return this.smartContracts.get(index);
    }

    // Getters y Setters
    public int getId() { return id; }
    public long getTimeStamp() { return timeStamp; }
    public void setTimeStamp(long timeStamp) { this.timeStamp = timeStamp; }
    public String getPreviousHash() { return previousHash; }
    public void setPreviousHash(String previousHash) { this.previousHash = previousHash; }
    public String getHash() { return hash; }
    public void setHash(String hash) { this.hash = hash; }
    public int getNonce() { return nonce; }
    public void setNonce(int nonce) { this.nonce = nonce; }
    public List<SmartContract> getSmartContracts() { return smartContracts; }

    @Override
    public String toString() {
        return "Bloque #" + id + " [Hash=" + hash + ", PrevHash=" + previousHash + ", Nonce=" + nonce + ", Contratos=" + smartContracts.size() + "]";
    }
}