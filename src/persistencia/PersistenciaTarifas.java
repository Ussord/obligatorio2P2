/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package persistencia;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import modelo.TarifaZona;
import modelo.enums.CategoriaPeso;
import modelo.enums.Zona;

/**
 *
 * @author Mauro
 */
public final class PersistenciaTarifas {

    private static final int CANTIDAD_PRECIOS_POR_ZONA = 4;
    private static final String SEPARADOR_ZONA = "#";
    private static final String SEPARADOR_PRECIOS = ",";

    private PersistenciaTarifas() {
    }

    public static List<TarifaZona> cargar(String archivo) {
        validarArchivo(archivo);
        try {
            List<String> lineas = Files.readAllLines(Path.of(archivo), StandardCharsets.UTF_8);
            ArrayList<TarifaZona> tarifas = new ArrayList<>();
            for (String linea : lineas) {
                if (!linea.trim().isEmpty()) {
                    tarifas.add(crearTarifaZonaDesdeLinea(linea));
                }
            }
            validarCantidadZonas(tarifas);
            return tarifas;
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo cargar el archivo de tarifas.", e);
        }
    }

    public static void guardar(String archivo, Collection<TarifaZona> tarifas) {
        validarArchivo(archivo);
        validarTarifas(tarifas);
        StringBuilder contenido = new StringBuilder();
        for (Zona zona : Zona.values()) {
            TarifaZona tarifaZona = buscarTarifaZona(tarifas, zona);
            contenido.append(formatearLinea(tarifaZona))
                    .append(System.lineSeparator());
        }
        try {
            Files.writeString(Path.of(archivo), contenido.toString(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo guardar el archivo de tarifas.", e);
        }
    }

    private static TarifaZona crearTarifaZonaDesdeLinea(String linea) {
        String[] partes = linea.split(SEPARADOR_ZONA);
        if (partes.length != 2) {
            throw new IllegalArgumentException("Formato incorrecto en línea de tarifas: " + linea);
        }
        Zona zona = Zona.valueOf(partes[0].trim().toUpperCase());
        int[] precios = obtenerPrecios(partes[1], linea);
        return new TarifaZona(
                zona,
                precios[0],
                precios[1],
                precios[2],
                precios[3]
        );
    }

    private static int[] obtenerPrecios(String textoPrecios, String lineaOriginal) {
        String[] partesPrecios = textoPrecios.split(SEPARADOR_PRECIOS);
        if (partesPrecios.length != CANTIDAD_PRECIOS_POR_ZONA) {
            throw new IllegalArgumentException(
                    "Cantidad incorrecta de precios en línea: " + lineaOriginal
            );
        }
        int[] precios = new int[CANTIDAD_PRECIOS_POR_ZONA];
        for (int i = 0; i < partesPrecios.length; i++) {
            precios[i] = convertirPrecio(partesPrecios[i], lineaOriginal);
        }
        return precios;
    }

    private static int convertirPrecio(String textoPrecio, String lineaOriginal) {
        try {
            int precio = Integer.parseInt(textoPrecio.trim());
            if (precio < 0) {
                throw new IllegalArgumentException(
                        "Precio negativo en línea de tarifas: " + lineaOriginal
                );
            }
            return precio;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Precio inválido en línea de tarifas: " + lineaOriginal,
                    e
            );
        }
    }

    private static String formatearLinea(TarifaZona tarifaZona) {
        return tarifaZona.getZona() + SEPARADOR_ZONA
                + tarifaZona.precio(CategoriaPeso.MENOR_A_1_KG) + SEPARADOR_PRECIOS
                + tarifaZona.precio(CategoriaPeso.DESDE_1_HASTA_MENOS_5_KG) + SEPARADOR_PRECIOS
                + tarifaZona.precio(CategoriaPeso.DESDE_5_HASTA_MENOS_10_KG) + SEPARADOR_PRECIOS
                + tarifaZona.precio(CategoriaPeso.DE_10_KG_O_MAS);
    }

    private static TarifaZona buscarTarifaZona(Collection<TarifaZona> tarifas, Zona zona) {
        for (TarifaZona tarifaZona : tarifas) {
            if (tarifaZona.getZona() == zona) {
                return tarifaZona;
            }
        }
        throw new IllegalStateException("No hay tarifas cargadas para la zona " + zona + ".");
    }

    private static void validarCantidadZonas(ArrayList<TarifaZona> tarifas) {
        for (Zona zona : Zona.values()) {
            buscarTarifaZona(tarifas, zona);
        }
    }

    private static void validarArchivo(String archivo) {
        if (archivo == null || archivo.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del archivo no puede ser vacío.");
        }
    }

    private static void validarTarifas(Collection<TarifaZona> tarifas) {
        if (tarifas == null || tarifas.isEmpty()) {
            throw new IllegalArgumentException("La colección de tarifas no puede ser vacía.");
        }
    }
}
