/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.dtos;

import modelo.Cliente;

/**
 *
 * @author Mauro
 */
public class ConsultaPorCliente {

    private final Cliente cliente;
    private final int pendientes;
    private final int enviados;
    private final int recibidos;

    public ConsultaPorCliente(Cliente cliente, int pendientes, int enviados, int recibidos) {
        this.cliente = cliente;
        this.pendientes = pendientes;
        this.enviados = enviados;
        this.recibidos = recibidos;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public int getPendientes() {
        return pendientes;
    }

    public int getEnviados() {
        return enviados;
    }

    public int getRecibidos() {
        return recibidos;
    }

    public int getTotal() {
        return pendientes + enviados + recibidos;
    }
}
