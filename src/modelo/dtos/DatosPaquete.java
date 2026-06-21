/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.dtos;

import java.time.LocalDate;
import modelo.Cliente;
import modelo.enums.Departamento;

/**
 *
 * @author Mauro
 */
public class DatosPaquete {

    private final String identificador;
    private final Cliente cliente;
    private final LocalDate fechaIngreso;
    private final String destinatario;
    private final String direccion;
    private final Departamento departamentoDestino;
    private final int pesoGramos;

    public DatosPaquete(String identificador,
            Cliente cliente,
            LocalDate fechaIngreso,
            String destinatario,
            String direccion,
            Departamento departamentoDestino,
            int pesoGramos) {
        this.identificador = identificador;
        this.cliente = cliente;
        this.fechaIngreso = fechaIngreso;
        this.destinatario = destinatario;
        this.direccion = direccion;
        this.departamentoDestino = departamentoDestino;
        this.pesoGramos = pesoGramos;
    }

    public String getIdentificador() {
        return identificador;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public LocalDate getFechaIngreso() {
        return fechaIngreso;
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
}
