/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package blockchain;

import java.security.MessageDigest;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author owen7
 */
public class Cifrado {
    
  private SecretKeySpec llave;

    public Cifrado(String pClave) {
        try {
            MessageDigest oHash = MessageDigest.getInstance("SHA-256");
            byte[] aBytes = oHash.digest(pClave.getBytes("UTF-8"));
            this.llave = new SecretKeySpec(aBytes, "AES");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String encriptar(String pCadena) throws Exception {
        Cipher oCifrado = Cipher.getInstance("AES/ECB/PKCS5Padding");
        oCifrado.init(Cipher.ENCRYPT_MODE, this.llave);
        byte[] aBytes = pCadena.getBytes("UTF-8");
        byte[] aBytesEnc = oCifrado.doFinal(aBytes);
        return Base64.getEncoder().encodeToString(aBytesEnc);
    }

    public String desencriptar(String pCadena) throws Exception {
        Cipher oDescifrado = Cipher.getInstance("AES/ECB/PKCS5Padding");
        oDescifrado.init(Cipher.DECRYPT_MODE, this.llave);
        byte[] aBytes = Base64.getDecoder().decode(pCadena);
        byte[] aBytesDec = oDescifrado.doFinal(aBytes);
        return new String(aBytesDec);
    }
    
}
