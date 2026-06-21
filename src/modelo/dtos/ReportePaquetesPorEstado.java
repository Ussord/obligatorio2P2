/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.dtos;

import modelo.enums.EstadoPaquete;
import modelo.enums.Zona;

/**
 *
 * @author Mauro
 */
public class ReportePaquetesPorEstado {

    private final int[][] cantidades;

    public ReportePaquetesPorEstado(int[][] cantidades) {
        this.cantidades = cantidades;
    }

    public int getCantidad(Zona zona, EstadoPaquete estado) {
        return cantidades[zona.ordinal()][estado.ordinal()];
    }

    public int getTotal(Zona zona) {
        int total = 0;
        for (EstadoPaquete estado : EstadoPaquete.values()) {
            total += getCantidad(zona, estado);
        }

        return total;
    }
}
