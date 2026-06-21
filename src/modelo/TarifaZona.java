/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.io.Serializable;
import java.util.EnumMap;
import java.util.Map;
import modelo.enums.CategoriaPeso;
import modelo.enums.Zona;

/**
 *
 * @author Mauro
 */
public class TarifaZona implements Serializable {

    private static final long serialVersionUID = 1L;
    private final Zona zona;
    private final Map<CategoriaPeso, Integer> precios;

    public TarifaZona(Zona zona,
            int precioCategoria1,
            int precioCategoria2,
            int precioCategoria3,
            int precioCategoria4) {
        this.zona = zona;
        this.precios = new EnumMap<>(CategoriaPeso.class);
        precios.put(CategoriaPeso.MENOR_A_1_KG, precioCategoria1);
        precios.put(CategoriaPeso.DESDE_1_HASTA_MENOS_5_KG, precioCategoria2);
        precios.put(CategoriaPeso.DESDE_5_HASTA_MENOS_10_KG, precioCategoria3);
        precios.put(CategoriaPeso.DE_10_KG_O_MAS, precioCategoria4);
    }

    public Zona getZona() {
        return zona;
    }

    public int precio(CategoriaPeso categoria) {
        return precios.get(categoria);
    }

    public void actualizar(double porcentaje) {
        for (CategoriaPeso categoria : CategoriaPeso.values()) {
            int precioActual = precios.get(categoria);
            int nuevoPrecio = calcularPrecioActualizado(precioActual, porcentaje);
            precios.put(categoria, nuevoPrecio);
        }
    }

    private int calcularPrecioActualizado(int precioActual, double porcentaje) {
        double factor = 1 + porcentaje / 100.0;
        int nuevoPrecio = (int) Math.round(precioActual * factor);
        if (nuevoPrecio < 0) {
            nuevoPrecio = 0;
        }
        return nuevoPrecio;
    }

    public Map<CategoriaPeso, Integer> getPrecios() {
        return new EnumMap<>(precios);
    }
}
