/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.io.Serializable;

/**
 *
 * @author Mauro
 */
public abstract class Persona implements Serializable {

    private static final long serialVersionUID = 1L;
    private String nombre;
    private String celular;

    protected Persona(String nombre, String celular) {
        this.nombre = nombre;
        this.celular = celular;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCelular() {
        return celular;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    @Override
    public String toString() {
        return nombre;
    }
}
