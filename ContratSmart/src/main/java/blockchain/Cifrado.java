/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package blockchain;

import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author owen7
 */
public class Cifrado {
    
    private SecretKeySpec llave;
    private Cipher oCifrado;
    private Cipher oDescifrado;

    public Cifrado(String pClave) {
        try {
            MessageDigest oHash = MessageDigest.getInstance("SHA-1");
            byte[] aBytes = oHash.digest(pClave.getBytes("UTF-8"));
            byte[] aBytes32 = Arrays.copyOf(aBytes, 32);
            this.llave = new SecretKeySpec(aBytes32, "AES");

            this.oCifrado = Cipher.getInstance("AES/ECB/PKCS5Padding");
            this.oCifrado.init(Cipher.ENCRYPT_MODE, this.llave);

            this.oDescifrado = Cipher.getInstance("AES/ECB/PKCS5Padding");
            this.oDescifrado.init(Cipher.DECRYPT_MODE, this.llave);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String encriptar(String pCadena) throws Exception {
        byte[] aBytes = pCadena.getBytes("UTF-8");
        byte[] aBytesEnc = this.oCifrado.doFinal(aBytes);
        return Base64.getEncoder().encodeToString(aBytesEnc);
    }

    public String desencriptar(String pCadena) throws Exception {
        byte[] aBytes = Base64.getDecoder().decode(pCadena);
        byte[] aBytesDec = this.oDescifrado.doFinal(aBytes);
        return new String(aBytesDec);
    }
    
}
