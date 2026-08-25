package blockchain;

import java.io.Serializable;

public class SmartContract implements Serializable {

    private String idContrato;
    private String idEmpleado;
    private String nombreEmpleado;
    private String puesto;
    private double salario;
    private String sucursal;

    public SmartContract(String idContrato, String idEmpleado, String nombreEmpleado, String puesto, double salario, String sucursal) {
        this.idContrato = idContrato;
        this.idEmpleado = idEmpleado;
        this.nombreEmpleado = nombreEmpleado;
        this.puesto = puesto;
        this.salario = salario;
        this.sucursal = sucursal;
    }

    // Getters y Setters
    public String getIdContrato() { return idContrato; }
    public String getIdEmpleado() { return idEmpleado; }
    public String getNombreEmpleado() { return nombreEmpleado; }
    public String getPuesto() { return puesto; }
    public double getSalario() { return salario; }
    public String getSucursal() { return sucursal; }

    @Override
    public String toString() {
        return "Contrato [" + idContrato + "] - Empleado: " + nombreEmpleado + 
               " (ID: " + idEmpleado + ") | Puesto: " + puesto + 
               " | Salario: $" + salario + " | Sucursal: " + sucursal;
    }
}