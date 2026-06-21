/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Mauro
 */
public class LogTransacciones implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final DateTimeFormatter FORMATO_FECHA_HORA
            = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");
    private final String archivo;

    public LogTransacciones(String archivo) {
        this.archivo = archivo;
    }

    public void registrar(String descripcion) {
        validarDescripcion(descripcion);
        String fechaHora = LocalDateTime.now().format(FORMATO_FECHA_HORA);
        String linea = fechaHora + " - " + descripcion + System.lineSeparator();
        try {
            Files.writeString(
                    Path.of(archivo),
                    linea,
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND
            );

        } catch (IOException e) {
            throw new IllegalStateException("No se pudo registrar la transacción.", e);
        }
    }

    public String leer() {
        Path ruta = Path.of(archivo);
        try {
            if (Files.notExists(ruta)) {
                return "";
            }
            return Files.readString(ruta, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer el log de transacciones.", e);
        }
    }

    public void borrar() {
        try {
            Files.writeString(
                    Path.of(archivo),
                    "",
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo borrar el log de transacciones.", e);
        }
    }

    public String getArchivo() {
        return archivo;
    }

    private void validarDescripcion(String descripcion) {
        if (descripcion == null || descripcion.isBlank()) {
            throw new IllegalArgumentException("La descripción de la transacción no puede ser vacía.");
        }
    }
}
