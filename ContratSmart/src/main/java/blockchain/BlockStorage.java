/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package blockchain;

import java.io.File;
import java.io.FileWriter;

/**
 *
 * @author owen7
 */
public class BlockStorage {
    private static final String FOLDER_PATH = "./blockchain_data/";

    public BlockStorage() {
        File folder = new File(FOLDER_PATH);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }

    public void saveBlock(Block block, Cifrado cifrador) {
        try {
            // Estructura JSON del bloque
            String jsonRaw = "{\n"
                + "  \"id\": " + block.getId() + ",\n"
                + "  \"timeStamp\": " + block.getTimeStamp() + ",\n"
                + "  \"previousHash\": \"" + block.getPreviousHash() + "\",\n"
                + "  \"hash\": \"" + block.getHash() + "\",\n"
                + "  \"nonce\": " + block.getNonce() + "\n"
                + "}";

            // Encriptar JSON
            String jsonEncriptado = cifrador.encriptar(jsonRaw);

            // Guardar como <hash>.json
            String fileName = FOLDER_PATH + block.getHash() + ".json";
            try (FileWriter writer = new FileWriter(fileName)) {
                writer.write(jsonEncriptado);
            }
        } catch (Exception e) {
            System.err.println("Error al guardar JSON: " + e.getMessage());
        }
    }
}
 