/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package aplicacion;

import interfaces.VentanaInicio;
import interfaces.VentanaMenuPrincipal;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import modelo.SistemaEnvios;

/**
 *
 * @author Mauro
 */
public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::iniciarAplicacion);
    }

    private static void iniciarAplicacion() {
        configurarLookAndFeel();
        VentanaInicio ventanaInicio = new VentanaInicio(null, true);
        ventanaInicio.setLocationRelativeTo(null);
        ventanaInicio.setVisible(true);
        if (!ventanaInicio.isConfirmado()) {
            System.exit(0);
        }
        SistemaEnvios sistema = obtenerSistema(ventanaInicio);
        abrirMenuPrincipal(sistema);
    }

    private static SistemaEnvios obtenerSistema(VentanaInicio ventanaInicio) {
        try {
            return SistemaEnvios.cargarSistema(
                    ventanaInicio.isIniciarConDatosGuardados()
            );
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(
                    null,
                    "No se pudieron cargar los datos. Se iniciará un sistema nuevo.",
                    "Error al iniciar sistema",
                    JOptionPane.WARNING_MESSAGE
            );
            return SistemaEnvios.cargarSistema(false);
        }
    }

    private static void abrirMenuPrincipal(SistemaEnvios sistema) {
        VentanaMenuPrincipal ventanaMenu = new VentanaMenuPrincipal(sistema);
        ventanaMenu.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        ventanaMenu.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                cerrarAplicacion(ventanaMenu, sistema);
            }
        });
        ventanaMenu.setLocationRelativeTo(null);
        ventanaMenu.setVisible(true);
    }

    private static void cerrarAplicacion(JFrame ventana, SistemaEnvios sistema) {
        try {
            sistema.guardarSistema();
            ventana.dispose();
            System.exit(0);
        } catch (IllegalStateException e) {
            int opcion = JOptionPane.showConfirmDialog(
                    ventana,
                    "No se pudo guardar el sistema.\n¿Desea salir igualmente?",
                    "Error al guardar",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.ERROR_MESSAGE
            );
            if (opcion == JOptionPane.YES_OPTION) {
                ventana.dispose();
                System.exit(0);
            }
        }
    }

    private static void configurarLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Se usa lookandfeel por defecto
        }
    }
}
