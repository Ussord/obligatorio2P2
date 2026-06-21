/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import modelo.enums.EstadoPaquete;
import modelo.enums.Zona;

/**
 *
 * @author Mauro
 */
public class Envio implements Serializable {

    private static final long serialVersionUID = 1L;
    private final int numero;
    private final LocalDate fechaEnvio;
    private final Zona zona;
    private Funcionario funcionario;
    private final ArrayList<Paquete> paquetes;
    private boolean recepcionRegistrada;

    public Envio(int numero, LocalDate fechaEnvio, Zona zona, Funcionario funcionario) {
        this.numero = numero;
        this.fechaEnvio = fechaEnvio;
        this.zona = zona;
        this.funcionario = funcionario;
        this.paquetes = new ArrayList<>();
        this.recepcionRegistrada = false;
    }

    public int getNumero() {
        return numero;
    }

    public LocalDate getFechaEnvio() {
        return fechaEnvio;
    }

    public Zona getZona() {
        return zona;
    }

    public Funcionario getFuncionario() {
        return funcionario;
    }

    public boolean tieneRecepcionRegistrada() {
        return recepcionRegistrada;
    }

    public List<Paquete> getPaquetes() {
        return new ArrayList<>(paquetes);
    }

    public void agregarPaquete(Paquete paquete) {
        validarPuedeModificarse();
        validarPaquete(paquete);
        if (!paquete.estaPendiente()) {
            throw new IllegalArgumentException("El paquete debe estar pendiente.");
        }
        if (paquete.getZona() != zona) {
            throw new IllegalArgumentException("El paquete no pertenece a la zona del envío.");
        }
        if (paquetes.contains(paquete)) {
            throw new IllegalArgumentException("El paquete ya está en el envío.");
        }
        paquete.cambiarEstado(EstadoPaquete.ENVIADO);
        paquetes.add(paquete);
    }

    public void quitarPaquete(Paquete paquete) {
        validarPuedeModificarse();
        validarPaquete(paquete);
        if (paquetes.remove(paquete)) {
            paquete.cambiarEstado(EstadoPaquete.PENDIENTE);
        }
    }

    public int totalPesoGramos() {
        int total = 0;
        for (Paquete paquete : paquetes) {
            total += paquete.getPesoGramos();
        }
        return total;
    }

    public int totalMonto() {
        int total = 0;
        for (Paquete paquete : paquetes) {
            total += paquete.getPrecio();
        }
        return total;
    }

    public void registrarRecepcion(List<Paquete> paquetesEntregados) {
        if (recepcionRegistrada) {
            throw new IllegalStateException("La recepción de este envío ya fue registrada.");
        }
        if (paquetesEntregados == null) {
            throw new IllegalArgumentException("La lista de paquetes entregados no puede ser nula.");
        }
        validarPaquetesEntregadosPertenecenAlEnvio((ArrayList<Paquete>) paquetesEntregados);
        ArrayList<Paquete> paquetesOriginales = new ArrayList<>(paquetes);
        paquetes.clear();
        for (Paquete paquete : paquetesOriginales) {
            if (paquetesEntregados.contains(paquete)) {
                paquete.cambiarEstado(EstadoPaquete.RECIBIDO);
                paquetes.add(paquete);
            } else {
                paquete.cambiarEstado(EstadoPaquete.PENDIENTE);
            }
        }
        recepcionRegistrada = true;
    }

    private void validarPaquetesEntregadosPertenecenAlEnvio(ArrayList<Paquete> paquetesEntregados) {
        for (Paquete paquete : paquetesEntregados) {
            if (!paquetes.contains(paquete)) {
                throw new IllegalArgumentException("Hay paquetes entregados que no pertenecen al envío.");
            }
        }
    }

    private void validarPuedeModificarse() {
        if (recepcionRegistrada) {
            throw new IllegalStateException("No se puede modificar un envío con recepción registrada.");
        }
    }

    private void validarPaquete(Paquete paquete) {
        if (paquete == null) {
            throw new IllegalArgumentException("El paquete no puede ser null.");
        }
    }

    @Override
    public String toString() {
        return "Envío " + numero + " - " + fechaEnvio + " - " + zona;
    }
}
