/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package modelo.enums;

/**
 *
 * @author Mauro
 */
public enum Departamento {
    ARTIGAS(Zona.NORTE),
    SALTO(Zona.NORTE),
    PAYSANDU(Zona.NORTE),
    RIVERA(Zona.NORTE),
    TACUAREMBO(Zona.NORTE),
    RIO_NEGRO(Zona.OESTE),
    SORIANO(Zona.OESTE),
    COLONIA(Zona.OESTE),
    SAN_JOSE(Zona.OESTE),
    CERRO_LARGO(Zona.ESTE),
    TREINTA_Y_TRES(Zona.ESTE),
    LAVALLEJA(Zona.ESTE),
    ROCHA(Zona.ESTE),
    MALDONADO(Zona.ESTE),
    DURAZNO(Zona.SUR),
    FLORES(Zona.SUR),
    FLORIDA(Zona.SUR),
    CANELONES(Zona.SUR),
    MONTEVIDEO(Zona.SUR);

    private final Zona zona;

    private Departamento(Zona zona) {
        this.zona = zona;
    }

    public Zona getZona() {
        return zona;
    }
}
