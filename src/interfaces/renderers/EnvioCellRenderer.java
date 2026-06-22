/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package interfaces.renderers;

import java.awt.Color;
import java.awt.Component;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import modelo.Envio;

/**
 *
 * @author Mauro
 */
public class EnvioCellRenderer extends DefaultListCellRenderer {

    private static final Color COLOR_RECEPCIONADO = new Color(198, 239, 206);
    private static final Color COLOR_PENDIENTE = new Color(255, 242, 204);

    @Override
    public Component getListCellRendererComponent(
            JList<?> list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
        Component componente = super.getListCellRendererComponent(
                list,
                value,
                index,
                isSelected,
                cellHasFocus
        );
        if (value instanceof Envio envio && !isSelected) {
            if (envio.tieneRecepcionRegistrada()) {
                componente.setBackground(COLOR_RECEPCIONADO);
            } else {
                componente.setBackground(COLOR_PENDIENTE);
            }
        }
        return componente;
    }
}
