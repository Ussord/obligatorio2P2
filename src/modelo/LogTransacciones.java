/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.io.Serializable;
import persistencia.PersistenciaTransacciones;

/**
 *
 * @author Mauro
 */
public class LogTransacciones implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String archivo;

    public LogTransacciones(String archivo) {
        this.archivo = archivo;
    }

    public void registrar(String descripcion) {
        PersistenciaTransacciones.registrar(archivo, descripcion);
    }

    public String leer() {
        return PersistenciaTransacciones.leer(archivo);
    }

    public void borrar() {
        PersistenciaTransacciones.borrar(archivo);
    }

    public String getArchivo() {
        return archivo;
    }
}
