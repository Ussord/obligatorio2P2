/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author Mauro
 */
public class Funcionario extends Persona {

    private static final long serialVersionUID = 1L;
    private int numeroFuncionario;
    private int anioIngreso;

    public Funcionario(String nombre, String celular, int numeroFuncionario, int anioIngreso) {
        super(nombre, celular);
        this.numeroFuncionario = numeroFuncionario;
        this.anioIngreso = anioIngreso;
    }

    public int getNumeroFuncionario() {
        return numeroFuncionario;
    }

    public int getAnioIngreso() {
        return anioIngreso;
    }

    public void setNumeroFuncionario(int numeroFuncionario) {
        this.numeroFuncionario = numeroFuncionario;
    }

    public void setAnioIngreso(int anioIngreso) {
        this.anioIngreso = anioIngreso;
    }
}
