package forms;

import blockchain.BlockChain;
import blockchain.Block;
import blockchain.SmartContract;
import blockchain.Cifrado;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author owen7
 */
public class frmServer extends javax.swing.JFrame {

    private BlockChain bc;
    private Cifrado cifrador;
    private boolean escuchando = false;

    public frmServer() {
        initComponents();
        this.setLocationRelativeTo(null); // Centrar en pantalla
        
        // Regla 9: Inicializar Blockchain con Dificultad 3 (tres ceros '0')
        // Al instanciar BlockChain se mina automáticamente el Bloque Génesis
        this.bc = new BlockChain(4, "0");
        this.cifrador = new Cifrado("ClaveSecretaServidorBlockChain");
        
        jTextArea1.setText("=== SERVIDOR SUCURSAL BLOCKCHAIN INICIALIZADO ===\n");
        jTextArea1.append("Bloque Génesis Minado con éxito.\n");
        jTextArea1.append("Dificultad de Minado: 4 ceros '0'\n\n");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Servidor Blockchain - Registro de Contrataciones");

        jTextArea1.setColumns(20);
        jTextArea1.setRows(10);
        jTextArea1.setEditable(false);
        jScrollPane1.setViewportView(jTextArea1);

        jLabel1.setFont(new java.awt.Font("FreeMono", 1, 24)); // NOI18N
        jLabel1.setText("SERVIDOR SUCURSAL");

        jButton1.setText("Iniciar Escucha");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
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
                    .addComponent(jButton1))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>                        

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {                                         
        if (escuchando) {
            jTextArea1.append("El servidor ya está escuchando en el puerto 7000...\n");
            return;
        }

        escuchando = true;
        jButton1.setEnabled(false);
        jButton1.setText("Escuchando...");

        // Hilo en segundo plano para no congelar la interfaz Swing durante el minado
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(7000)) {
                jTextArea1.append(">> Servidor escuchando peticiones en puerto 7000...\n\n");

                while (true) {
                    Socket socket = serverSocket.accept();
                        DataInputStream inputStream =
                            new DataInputStream(socket.getInputStream());
                        DataOutputStream outputStream =
                            new DataOutputStream(socket.getOutputStream());
                    // Formato esperado de contratación: "ID_CONTRATO;ID_EMP;NOMBRE_EMP;PUESTO;SALARIO;SUCURSAL"
                    String rawMessage = inputStream.readUTF();

                    if (rawMessage.equalsIgnoreCase("EXIT")) {
                        jTextArea1.append(">> Comando EXIT recibido. Deteniendo Servidor.\n");
                        socket.close();
                        break;
                    }

                    jTextArea1.append("--------------------------------------------------\n");
                    jTextArea1.append(">> Solicitud de Contratación Recibida: " + rawMessage + "\n");

                    // 1. Parsear datos del contrato laboral
                    String[] partes = rawMessage.split(";");
                    String idContrato = partes.length > 0 ? partes[0] : "CTR-000";
                    String idEmpleado = partes.length > 1 ? partes[1] : "EMP-000";
                    String nombreEmpleado = partes.length > 2 ? partes[2] : "Anonimo";
                    String puesto = partes.length > 3 ? partes[3] : "General";
                    double salario = partes.length > 4 ? Double.parseDouble(partes[4]) : 0.0;
                    String sucursal = partes.length > 5 ? partes[5] : "Central";

                    // 2. Cifrar datos sensibles del empleado con AES
                    String idEmpCifrado = cifrador.encriptar(idEmpleado);
                    String nombreCifrado = cifrador.encriptar(nombreEmpleado);

                    jTextArea1.append(">> Identidades del Empleado Cifradas (AES):\n");
                    jTextArea1.append("   ID Empleado Cifrado: " + idEmpCifrado + "\n");
                    jTextArea1.append("   Nombre Cifrado: " + nombreCifrado + "\n");

                    // 3. Crear nuevo bloque encadenado y añadir el Smart Contract laboral
                    Block nuevoBloque = bc.createBlock();
                    SmartContract contrato = new SmartContract(idContrato, idEmpCifrado, nombreCifrado, puesto, salario, sucursal);
                    nuevoBloque.addSmartContract(contrato);

                    // 4. Minar el nuevo bloque con Proof of Work
                    jTextArea1.append(">> Minando Bloque #" + nuevoBloque.getId() + "...\n");
                    long tInicio = System.currentTimeMillis();
                    bc.mineBlock(nuevoBloque);
                    long tFin = System.currentTimeMillis();

                    String respuesta =
                            "OK;"
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

                    // Mostrar el estado público de la cadena completa
                    jTextArea1.append("=== REGISTRO GENERAL DE CONTRATACIONES ===\n");
                    jTextArea1.append(bc.toString());
                    jTextArea1.append("--------------------------------------------------\n\n");

                    inputStream.close();
                    socket.close();
                }
            } catch (Exception e) {
                jTextArea1.append("Error en el servidor: " + e.getMessage() + "\n");
            }
        }).start();
    }                                        

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            new frmServer().setVisible(true);
        });
    }

    // Variables declaration - do not modify                     
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration                   
}