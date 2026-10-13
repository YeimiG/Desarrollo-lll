/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package forms;

import blockchain.BlockChain;
import blockchain.Block;
import blockchain.SmartContract;
import blockchain.Cifrado;
import blockchain.BlockStorage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
/**
 *
 * @author owen7
 */
public class frmServerStatic1 extends javax.swing.JFrame {

    private BlockChain bc;
    private Cifrado cifrador;
    private boolean escuchando = false;

    public frmServerStatic1() {
        initComponents();
        this.setLocationRelativeTo(null);
        
        this.bc = new BlockChain(4, "0");
        this.cifrador = new Cifrado("ClaveSecretaServidorBlockChain");
        
        //Prueba Validadicion de bloque 
        /*System.out.println("===Inicio pruebas de validadion===");
        Block genesis = bc.getChain().get(0);
        Block testBlock = new Block(1, genesis.getHash());
        bc.mineBlock(testBlock);
        System.out.println("hash del genesis:" + genesis.getHash());
        System.out.println("Hash del bloque de prueba: " + testBlock.getHash());
        boolean resultadoValido = bc.isValidBlock(testBlock, genesis);

        System.out.println("PRUEBA 1 - BLOQUE VALIDO: " + resultadoValido);

        // Alterar el nonce sin volver a minar el bloque.
        testBlock.setNonce(testBlock.getNonce() + 1);
        boolean resultadoAlterado = bc.isValidBlock(testBlock, genesis);
        System.out.println("PRUEBA 2 - BLOQUE ALTERADO: " + resultadoAlterado);
        System.out.println("=== FIN PRUEBAS DE VALIDACION ===");
        */
        jTextArea1.setText("=== SERVIDOR SECUNDARIO (NODO 2) BLOCKCHAIN INICIALIZADO ===\n");
        jTextArea1.append("Bloque Génesis Minado con éxito.\n");
        jTextArea1.append("Escuchando en Puerto Secundario: 7001\n");
        jTextArea1.append("Dificultad de Minado: 4 ceros '0'\n\n");
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Servidor Blockchain Secundario - Nodo 2 (Puerto 7001)");

        jTextArea1.setColumns(20);
        jTextArea1.setRows(10);
        jTextArea1.setEditable(false);
        jScrollPane1.setViewportView(jTextArea1);

        jLabel1.setFont(new java.awt.Font("FreeMono", 1, 24));
        jLabel1.setText("SERVIDOR NODO 2");

        jButton1.setText("Iniciar Escucha (7001)");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Abrir Sucursal");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton1))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 500, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(24, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        pack();
    }

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        frmPrincipal ventanaSucursal = new frmPrincipal();
        ventanaSucursal.setPuertoServidor(7001);
        ventanaSucursal.setVisible(true);
        jTextArea1.append(">> Nueva ventana de Sucursal apuntando al Puerto 7001 iniciada.\n");
    }

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        if (escuchando) {
            jTextArea1.append("El servidor secundario ya está escuchando en el puerto 7001...\n");
            return;
        }

        escuchando = true;
        jButton1.setEnabled(false);
        jButton1.setText("Escuchando (7001)");

        new Thread(() -> {
            BlockStorage storage = new BlockStorage();

            try (ServerSocket serverSocket = new ServerSocket(7001)) {
                jTextArea1.append(">> Servidor Secundario (Nodo 2) escuchando en puerto 7001...\n\n");

                while (true) {
                    Socket socket = serverSocket.accept();
                    DataInputStream inputStream = new DataInputStream(socket.getInputStream());
                    DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());
                    
                    String rawMessage = inputStream.readUTF();

                    if (rawMessage.equalsIgnoreCase("EXIT")) {
                        jTextArea1.append(">> Comando EXIT recibido. Deteniendo Servidor Secundario.\n");
                        socket.close();
                        break;
                    }

                    if (rawMessage.startsWith("BUSCAR;")) {
                        String idBuscado = rawMessage.split(";")[1];
                        jTextArea1.append(">> Buscando contrato en la Blockchain para ID: " + idBuscado + "\n");
                        
                        String idCifrado = cifrador.encriptar(idBuscado);
                        boolean encontrado = false;
                        
                        for (Block b : bc.getChain()) {
                            for (SmartContract sc : b.getSmartContracts()) {
                                if (sc.getIdEmpleado().equals(idCifrado)) {
                                    String nombreDescifrado = cifrador.desencriptar(sc.getNombreEmpleado());
                                    String respuesta = "ENCONTRADO;" + sc.getIdContrato() + ";" + nombreDescifrado + ";" + sc.getSucursal() + ";" + b.getId() + ";" + b.getHash();
                                    outputStream.writeUTF(respuesta);
                                    outputStream.flush();
                                    
                                    encontrado = true;
                                    jTextArea1.append("   - ¡Contrato encontrado en Bloque #" + b.getId() + "!\n");
                                    break;
                                }
                            }
                            if (encontrado) break;
                        }
                        
                        if (!encontrado) {
                            outputStream.writeUTF("NO_ENCONTRADO");
                            outputStream.flush();
                            jTextArea1.append("   - Contrato no encontrado.\n");
                        }

                    // =========================================================================
                    // UNICO CAMBIO AGREGADO: Manejar la señal de propagación sin volver a minar
                    // =========================================================================
                    } else if (rawMessage.startsWith("PROPAGAR_BLOQUE;")) {
                        String[] partes = rawMessage.split(";");
                        String idBloque = partes.length > 1 ? partes[1] : "?";
                        String hashBloque = partes.length > 2 ? partes[2] : "?";
                        
                        jTextArea1.append(">> [NOTIFICACIÓN P2P] El otro nodo minó el Bloque #" + idBloque + " (" + hashBloque + ")\n\n");
                    // =========================================================================

                    } else {
                        jTextArea1.append("--------------------------------------------------\n");
                        jTextArea1.append(">> Solicitud de Contratación Recibida en Nodo 2: " + rawMessage + "\n");

                        String[] partes = rawMessage.split(";");
                        String idContrato = partes.length > 0 ? partes[0] : "CTR-000";
                        String idEmpleado = partes.length > 1 ? partes[1] : "EMP-000";
                        String nombreEmpleado = partes.length > 2 ? partes[2] : "Anonimo";
                        String puesto = partes.length > 3 ? partes[3] : "General";
                        double salario = partes.length > 4 ? Double.parseDouble(partes[4]) : 0.0;
                        String sucursal = partes.length > 5 ? partes[5] : "Central";

                        String idEmpCifrado = cifrador.encriptar(idEmpleado);
                        String nombreCifrado = cifrador.encriptar(nombreEmpleado);

                        jTextArea1.append(">> Identidades del Empleado Cifradas (AES):\n");
                        jTextArea1.append("   ID Empleado Cifrado: " + idEmpCifrado + "\n");
                        jTextArea1.append("   Nombre Cifrado: " + nombreCifrado + "\n");

                        Block nuevoBloque = bc.createBlock();
                        SmartContract contrato = new SmartContract(idContrato, idEmpCifrado, nombreCifrado, puesto, salario, sucursal);
                        nuevoBloque.addSmartContract(contrato);

                        jTextArea1.append(">> Minando Bloque #" + nuevoBloque.getId() + " en Nodo 2...\n");
                        long tInicio = System.currentTimeMillis();
                        bc.mineBlock(nuevoBloque);
                        long tFin = System.currentTimeMillis();

                        storage.saveBlock(nuevoBloque, cifrador);
                        jTextArea1.append(">> Bloque persistido en disco: ./blockchain_data/" + nuevoBloque.getHash() + ".json\n");

                        propagarABloqueServidorPrincipal(nuevoBloque);

                        String respuesta = "OK;"
                                + nuevoBloque.getId() + ";"
                                + nuevoBloque.getPreviousHash() + ";"
                                + nuevoBloque.getHash() + ";"
                                + nuevoBloque.getNonce() + ";"
                                + 4 + ";"
                                + (tFin - tInicio) + ";"
                                + idEmpCifrado + ";"
                                + nombreCifrado;

                        outputStream.writeUTF(respuesta);
                        outputStream.flush();

                        jTextArea1.append("=== REGISTRO GENERAL DE CONTRATACIONES (NODO 2) ===\n");
                        jTextArea1.append(bc.toString());
                        jTextArea1.append("--------------------------------------------------\n\n");
                    }

                    inputStream.close();
                    socket.close();
                }
            } catch (Exception e) {
                jTextArea1.append("Error en Servidor Secundario: " + e.getMessage() + "\n");
            }
        }).start();
    }                                        

    private void propagarABloqueServidorPrincipal(Block bloque) {
        new Thread(() -> {
            try (Socket socketPeer = new Socket("127.0.0.1", 7000);
                 DataOutputStream outPeer = new DataOutputStream(socketPeer.getOutputStream())) {
                outPeer.writeUTF("PROPAGAR_BLOQUE;" + bloque.getId() + ";" + bloque.getHash());
                outPeer.flush();
            } catch (Exception e) {
                System.out.println("Nodo 1 no activo para recibir propagación: " + e.getMessage());
            }
        }).start();
    }

    public static void main(String args[]) {
        System.out.println("PRUEBA: SE EJECUTA frmServerStatic1");
        java.awt.EventQueue.invokeLater(() -> {
            new frmServerStatic1().setVisible(true);
        });
    }

    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
}