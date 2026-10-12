/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package forms;

import blockchain.Block;
import blockchain.Cifrado;
import blockchain.SmartContract;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import javax.swing.JOptionPane;

/**
 *
 * @author owen7
 */
/**
 * Aplicación Wallet / Cliente para interactuar con la Blockchain.
 * Envía transacciones y consulta contratos/datos con cifrado de red.
 */
public class frmWallet extends javax.swing.JFrame {

    private Cifrado cifrador;
    private String serverIp = "127.0.0.1";

    public frmWallet() {
        initComponents();
        this.setLocationRelativeTo(null);
        // Misma clave de cifrado AES que los Servidores
        this.cifrador = new Cifrado("ClaveSecretaServidorBlockChain");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        jLabelTitle = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        txtIdEmpleado = new javax.swing.JTextField();
        btnConsultar = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtResultado = new javax.swing.JTextArea();
        jLabelPuerto = new javax.swing.JLabel();
        comboPuerto = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Wallet Blockchain - Cliente Multinodo");

        jLabelTitle.setFont(new java.awt.Font("FreeMono", 1, 20)); 
        jLabelTitle.setText("WALLET BLOCKCHAIN");

        jLabel1.setText("ID Empleado a Consultar:");

        jLabelPuerto.setText("Nodo / Puerto:");

        // Selector dinámico para los puertos de los servidores P2P
        comboPuerto.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "7000 (Nodo 1)", "7001 (Nodo 2)" }));

        btnConsultar.setText("Consultar en Red");
        btnConsultar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConsultarActionPerformed(evt);
            }
        });

        txtResultado.setColumns(20);
        txtResultado.setRows(8);
        txtResultado.setEditable(false);
        jScrollPane1.setViewportView(txtResultado);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabelTitle)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabelPuerto))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(comboPuerto, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtIdEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(btnConsultar, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1))
                .addContainerGap(20, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jLabelTitle)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelPuerto)
                    .addComponent(comboPuerto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtIdEmpleado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnConsultar))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>                        

    // Evento al presionar "Consultar en Red"
    private void btnConsultarActionPerformed(java.awt.event.ActionEvent evt) {
        String idEmpleado = txtIdEmpleado.getText().trim();

        if (idEmpleado.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese un ID de Empleado válido.");
            return;
        }

        // Obtener el puerto seleccionado en el ComboBox (7000 o 7001)
        String seleccion = comboPuerto.getSelectedItem().toString();
        int puertoSeleccionado = seleccion.startsWith("7000") ? 7000 : 7001;

        txtResultado.setText("Conectando al Nodo en Puerto " + puertoSeleccionado + "...\n");

        try (Socket socket = new Socket(serverIp, puertoSeleccionado);
             DataOutputStream out = new DataOutputStream(socket.getOutputStream());
             DataInputStream in = new DataInputStream(socket.getInputStream())) {

            // 1. Enviar petición formateada de búsqueda cifrada
            String tramaPeticion = "BUSCAR;" + idEmpleado;
            out.writeUTF(tramaPeticion);
            out.flush();

            // 2. Recibir la respuesta enviada por el Servidor seleccionado
            String respuesta = in.readUTF();

            if (respuesta.startsWith("ENCONTRADO;")) {
                String[] partes = respuesta.split(";");
                StringBuilder sb = new StringBuilder();
                sb.append("=== CONTRATO ENCONTRADO (Nodo Puerto ").append(puertoSeleccionado).append(") ===\n");
                sb.append("ID Contrato: ").append(partes[1]).append("\n");
                sb.append("Nombre Empleado: ").append(partes[2]).append("\n");
                sb.append("Sucursal: ").append(partes[3]).append("\n");
                sb.append("Bloque #: ").append(partes[4]).append("\n");
                sb.append("Hash Bloque: ").append(partes[5]).append("\n");
                txtResultado.setText(sb.toString());
            } else {
                txtResultado.setText("Contrato NO encontrado en el Nodo (Puerto " + puertoSeleccionado + ") para el ID: " + idEmpleado);
            }

        } catch (Exception ex) {
            txtResultado.setText("Error al conectar con el Servidor en puerto " + puertoSeleccionado + ": " + ex.getMessage());
        }
    }

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            new frmWallet().setVisible(true);
        });
    }

    // Variables declaration - do not modify                     
    private javax.swing.JButton btnConsultar;
    private javax.swing.JComboBox<String> comboPuerto;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabelPuerto;
    private javax.swing.JLabel jLabelTitle;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea txtResultado;
    private javax.swing.JTextField txtIdEmpleado;
    // End of variables declaration                     
}