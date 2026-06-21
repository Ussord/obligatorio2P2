/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package persistencia;

import java.io.IOException;
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
public final class PersistenciaTransacciones {

    private static final DateTimeFormatter FORMATO_FECHA_HORA
            = DateTimeFormatter.ofPattern("dd/MM/yy HH:mm");

    private PersistenciaTransacciones() {
    }

    public static void registrar(String archivo, String descripcion) {
        validarArchivo(archivo);
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

    public static String leer(String archivo) {
        validarArchivo(archivo);
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

    public static void borrar(String archivo) {
        validarArchivo(archivo);
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

    private static void validarArchivo(String archivo) {
        if (archivo == null || archivo.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del archivo no puede ser vacío.");
        }
    }

    private static void validarDescripcion(String descripcion) {
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción de la transacción no puede ser vacía.");
        }
    }
}
