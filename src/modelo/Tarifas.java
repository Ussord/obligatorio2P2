/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import modelo.enums.CategoriaPeso;
import modelo.enums.Departamento;
import modelo.enums.Zona;

/**
 *
 * @author Mauro
 */
public class Tarifas implements Serializable {

    private static final long serialVersionUID = 1L;
    private final Map<Zona, TarifaZona> tarifasPorZona;

    public Tarifas() {
        this.tarifasPorZona = new EnumMap<>(Zona.class);
    }

    public int calcularPrecio(Departamento departamentoDestino, int pesoGramos) {
        validarDepartamento(departamentoDestino);
        Zona zona = departamentoDestino.getZona();
        CategoriaPeso categoria = CategoriaPeso.desdeGramos(pesoGramos);
        TarifaZona tarifaZona = tarifasPorZona.get(zona);
        if (tarifaZona == null) {
            throw new IllegalStateException("No hay tarifas cargadas para la zona " + zona + ".");
        }
        return tarifaZona.precio(categoria);
    }

    public void actualizarTarifas(double porcentaje) {
        for (TarifaZona tarifaZona : tarifasPorZona.values()) {
            tarifaZona.actualizar(porcentaje);
        }
    }

    public void cargarDesdeArchivo(String archivo) {
        try {
            List<String> lineas = Files.readAllLines(Path.of(archivo), StandardCharsets.UTF_8);
            tarifasPorZona.clear();
            for (String linea : lineas) {
                if (!linea.isBlank()) {
                    TarifaZona tarifaZona = crearTarifaZonaDesdeLinea(linea);
                    tarifasPorZona.put(tarifaZona.getZona(), tarifaZona);
                }
            }
            validarTarifasCompletas();
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo cargar el archivo de tarifas.", e);
        }
    }

    public void guardarEnArchivo(String archivo) {
        StringBuilder contenido = new StringBuilder();
        for (Zona zona : Zona.values()) {
            TarifaZona tarifaZona = tarifasPorZona.get(zona);
            if (tarifaZona == null) {
                throw new IllegalStateException("No hay tarifas cargadas para la zona " + zona + ".");
            }
            contenido.append(formatearLinea(tarifaZona))
                    .append(System.lineSeparator());
        }
        try {
            Files.writeString(Path.of(archivo), contenido.toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo guardar el archivo de tarifas.", e);
        }
    }

    public TarifaZona getTarifaZona(Zona zona) {
        return tarifasPorZona.get(zona);
    }

    public Map<Zona, TarifaZona> getTarifasPorZona() {
        return new EnumMap<>(tarifasPorZona);
    }

    private TarifaZona crearTarifaZonaDesdeLinea(String linea) {
        String[] partes = linea.split("#");
        if (partes.length != 2) {
            throw new IllegalArgumentException("Formato incorrecto en línea de tarifas: " + linea);
        }
        Zona zona = Zona.valueOf(partes[0].trim().toUpperCase());
        String[] precios = partes[1].split(",");
        if (precios.length != 4) {
            throw new IllegalArgumentException("Cantidad incorrecta de precios en línea: " + linea);
        }
        return new TarifaZona(
                zona,
                Integer.parseInt(precios[0].trim()),
                Integer.parseInt(precios[1].trim()),
                Integer.parseInt(precios[2].trim()),
                Integer.parseInt(precios[3].trim())
        );
    }

    private String formatearLinea(TarifaZona tarifaZona) {
        return tarifaZona.getZona() + "#"
                + tarifaZona.precio(CategoriaPeso.MENOR_A_1_KG) + ","
                + tarifaZona.precio(CategoriaPeso.DESDE_1_HASTA_MENOS_5_KG) + ","
                + tarifaZona.precio(CategoriaPeso.DESDE_5_HASTA_MENOS_10_KG) + ","
                + tarifaZona.precio(CategoriaPeso.DE_10_KG_O_MAS);
    }

    private void validarDepartamento(Departamento departamentoDestino) {
        if (departamentoDestino == null) {
            throw new IllegalArgumentException("El departamento destino no puede ser null.");
        }
    }

    private void validarTarifasCompletas() {
        for (Zona zona : Zona.values()) {
            if (!tarifasPorZona.containsKey(zona)) {
                throw new IllegalStateException("Faltan tarifas para la zona " + zona + ".");
            }
        }
    }
}
