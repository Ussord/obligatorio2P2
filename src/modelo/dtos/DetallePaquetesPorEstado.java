/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo.dtos;

import java.util.ArrayList;
import java.util.List;
import modelo.enums.Departamento;

/**
 *
 * @author Mauro
 */
public class DetallePaquetesPorEstado {

    private final int cantidadClientesDiferentes;
    private final ArrayList<Departamento> departamentosDestino;

    public DetallePaquetesPorEstado(int cantidadClientesDiferentes,
            List<Departamento> departamentosDestino) {
        this.cantidadClientesDiferentes = cantidadClientesDiferentes;
        this.departamentosDestino = new ArrayList<>(departamentosDestino);
    }

    public int getCantidadClientesDiferentes() {
        return cantidadClientesDiferentes;
    }

    public List<Departamento> getDepartamentosDestino() {
        return departamentosDestino;
    }
}
