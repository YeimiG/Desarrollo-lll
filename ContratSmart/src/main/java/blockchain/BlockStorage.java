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
            // Construir el JSON completo incluyendo el arreglo de SmartContracts
            StringBuilder jsonBuilder = new StringBuilder();
            jsonBuilder.append("{\n")
                .append("  \"id\": ").append(block.getId()).append(",\n")
                .append("  \"timeStamp\": ").append(block.getTimeStamp()).append(",\n")
                .append("  \"previousHash\": \"").append(block.getPreviousHash()).append("\",\n")
                .append("  \"hash\": \"").append(block.getHash()).append("\",\n")
                .append("  \"nonce\": ").append(block.getNonce()).append(",\n")
                .append("  \"smartContracts\": [");
            
            for (int i = 0; i < block.countSmartContracts(); i++) {
                SmartContract sc = block.getSmartContract(i);
                jsonBuilder.append("{")
                    .append("\"idContrato\":\"").append(sc.getIdContrato()).append("\",")
                    .append("\"idEmpleado\":\"").append(sc.getIdEmpleado()).append("\",")
                    .append("\"nombreEmpleado\":\"").append(sc.getNombreEmpleado()).append("\",")
                    .append("\"puesto\":\"").append(sc.getPuesto()).append("\",")
                    .append("\"salario\":").append(sc.getSalario()).append(",")
                    .append("\"sucursal\":\"").append(sc.getSucursal()).append("\"}");
                if (i < block.countSmartContracts() - 1) jsonBuilder.append(",");
            }
            jsonBuilder.append("]\n}");
            
            // Encriptar JSON y guardar
            String jsonEncriptado = cifrador.encriptar(jsonBuilder.toString());
            String fileName = FOLDER_PATH + block.getHash() + ".json";
            try (java.io.FileWriter writer = new java.io.FileWriter(fileName)) {
                writer.write(jsonEncriptado);
            }
        } catch (Exception e) {
            System.err.println("Error al guardar JSON: " + e.getMessage());
        }
    }
}
 