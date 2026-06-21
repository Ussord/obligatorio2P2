/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package persistencia;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import aplicacion.Main;
import modelo.SistemaEnvios;

/**
 *
 * @author Mauro
 */
public final class PersistenciaSistema {

    private PersistenciaSistema() {
    }

    public static void guardar(String archivo, SistemaEnvios sistema) {
        validarArchivo(archivo);
        validarSistema(sistema);
        try (ObjectOutputStream salida = new ObjectOutputStream(
                new FileOutputStream(archivo))) {
            salida.writeObject(sistema);
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo guardar el sistema.", e);
        }
    }

    public static SistemaEnvios cargar(String archivo) {
        validarArchivo(archivo);
        try (ObjectInputStream entrada = new ObjectInputStream(
                new FileInputStream(archivo))) {
            return (SistemaEnvios) entrada.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new IllegalStateException("No se pudo cargar el sistema.", e);
        }
    }

    public static boolean existeArchivo(String archivo) {
        validarArchivo(archivo);
        return Files.exists(Path.of(archivo));
    }

    private static void validarArchivo(String archivo) {
        if (archivo == null || archivo.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del archivo no puede ser vacío.");
        }
    }

    private static void validarSistema(SistemaEnvios sistema) {
        if (sistema == null) {
            throw new IllegalArgumentException("El sistema no puede ser null.");
        }
    }
}
