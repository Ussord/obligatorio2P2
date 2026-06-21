/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author Mauro
 */
import java.io.Serializable;
import java.time.LocalDate;
import modelo.dtos.DatosPaquete;
import modelo.enums.Departamento;
import modelo.enums.EstadoPaquete;
import modelo.enums.Zona;

public class Paquete implements Serializable {

    private static final long serialVersionUID = 1L;
    private final String identificador;
    private final LocalDate fechaIngreso;
    private final Cliente cliente;
    private final String destinatario;
    private final String direccion;
    private final Departamento departamentoDestino;
    private final int pesoGramos;
    private final int precio;
    private EstadoPaquete estado;

    public Paquete(DatosPaquete datos, int precio) {
        this.identificador = datos.getIdentificador();
        this.cliente = datos.getCliente();
        this.fechaIngreso = datos.getFechaIngreso();
        this.destinatario = datos.getDestinatario();
        this.direccion = datos.getDireccion();
        this.departamentoDestino = datos.getDepartamentoDestino();
        this.pesoGramos = datos.getPesoGramos();
        this.precio = precio;
        this.estado = EstadoPaquete.PENDIENTE;
    }

    public String getIdentificador() {
        return identificador;
    }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public String getDestinatario() {
        return destinatario;
    }

    public String getDireccion() {
        return direccion;
    }

    public Departamento getDepartamentoDestino() {
        return departamentoDestino;
    }

    public int getPesoGramos() {
        return pesoGramos;
    }

    public int getPrecio() {
        return precio;
    }

    public EstadoPaquete getEstado() {
        return estado;
    }

    public Zona getZona() {
        return departamentoDestino.getZona();
    }

    public void cambiarEstado(EstadoPaquete estado) {
        this.estado = estado;
    }

    public boolean estaPendiente() {
        return estado == EstadoPaquete.PENDIENTE;
    }

    @Override
    public String toString() {
        return identificador + " - " + destinatario + " - " + departamentoDestino;
    }
}
