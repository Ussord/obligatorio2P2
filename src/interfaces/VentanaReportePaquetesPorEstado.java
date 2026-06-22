/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package interfaces;

import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import modelo.SistemaEnvios;
import modelo.dtos.DetallePaquetesPorEstado;
import modelo.dtos.ReportePaquetesPorEstado;
import modelo.enums.Departamento;
import modelo.enums.EstadoPaquete;
import modelo.enums.TipoEvento;
import modelo.enums.Zona;

/**
 *
 * @author Mauro Russo
 */
@SuppressWarnings("java:S1200")
public class VentanaReportePaquetesPorEstado extends JFrame implements PropertyChangeListener {

    private final SistemaEnvios sistema;
    private ReportePaquetesPorEstado reporteActual;
    private static final java.awt.Color COLOR_ZONA_NORMAL = new java.awt.Color(255, 255, 0);
    private static final java.awt.Color COLOR_ZONA_HOVER = new java.awt.Color(102, 217, 232);

    /**
     * Creates new form VentanaReportePaquetesPorEstado
     *
     * @param sistema
     */
    public VentanaReportePaquetesPorEstado(SistemaEnvios sistema) {
        if (sistema == null) {
            throw new IllegalArgumentException("El sistema no puede ser null.");
        }
        this.sistema = sistema;
        initComponents();
        sistema.addPropertyChangeListener(this);
        configurarTabla();
        configurarDetalle();
        SwingUtilities.invokeLater(this::configurarMapa);
        cargarReporte();
        limpiarDetalle();
    }

    private void mostrarResumenZona(Zona zona) {
        if (reporteActual == null || zona == null) {
            limpiarDetalle();
            return;
        }
        txtDetalle.setText(
                "Zona: " + zona + "\n\n"
                + "Pendientes: " + reporteActual.getCantidad(zona, EstadoPaquete.PENDIENTE) + "\n"
                + "Enviados: " + reporteActual.getCantidad(zona, EstadoPaquete.ENVIADO) + "\n"
                + "Recibidos: " + reporteActual.getCantidad(zona, EstadoPaquete.RECIBIDO) + "\n"
                + "Total: " + reporteActual.getTotal(zona)
        );
        txtDetalle.setCaretPosition(0);
    }

    private void mostrarVentanaZona(Zona zona) {
        if (reporteActual == null || zona == null) {
            return;
        }
        String mensaje = "Pendientes: "
                + reporteActual.getCantidad(zona, EstadoPaquete.PENDIENTE)
                + "\nEnviados: "
                + reporteActual.getCantidad(zona, EstadoPaquete.ENVIADO)
                + "\nRecibidos: "
                + reporteActual.getCantidad(zona, EstadoPaquete.RECIBIDO)
                + "\nTotal: "
                + reporteActual.getTotal(zona);
        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Datos de la zona " + formatearZona(zona),
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private String formatearZona(Zona zona) {
        String texto = zona.name().toLowerCase();
        return texto.substring(0, 1).toUpperCase() + texto.substring(1);
    }

    private void marcarZonaSeleccionada(javax.swing.JLabel label) {
        label.setBackground(COLOR_ZONA_HOVER);
    }

    private void desmarcarZona(javax.swing.JLabel label) {
        label.setBackground(COLOR_ZONA_NORMAL);
    }

    private void configurarMapa() {
        java.net.URL urlImagen = getClass().getResource("/interfaz/imagenes/mapa.png");
        if (urlImagen == null) {
            throw new IllegalStateException("No se encontró la imagen del mapa.");
        }
        ImageIcon iconoMapa = new ImageIcon(urlImagen);
        int anchoLabel = lblMapa.getWidth();
        int altoLabel = lblMapa.getHeight();
        Image imagenEscalada = iconoMapa.getImage().getScaledInstance(
                anchoLabel,
                altoLabel,
                Image.SCALE_SMOOTH
        );
        lblMapa.setIcon(new ImageIcon(imagenEscalada));
        lblMapa.setText("");
    }

    private void configurarTabla() {
        DefaultTableModel modeloTabla = new DefaultTableModel(
                new Object[][]{},
                new String[]{
                    "Zona",
                    "Pendiente",
                    "Enviado",
                    "Recibido",
                    "Total"
                }
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tablaReporte.setModel(modeloTabla);
        tablaReporte.setCellSelectionEnabled(true);
        tablaReporte.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaReporte.getTableHeader().setReorderingAllowed(false);
    }

    private void actualizarDatos() {
        cargarReporte();
        limpiarDetalle();
    }

    private EstadoPaquete estadoPorColumna(int columna) {
        return switch (columna) {
            case 1 ->
                EstadoPaquete.PENDIENTE;
            case 2 ->
                EstadoPaquete.ENVIADO;
            case 3 ->
                EstadoPaquete.RECIBIDO;
            default ->
                null;
        };
    }

    private void configurarDetalle() {
        txtDetalle.setEditable(false);
        txtDetalle.setLineWrap(true);
        txtDetalle.setWrapStyleWord(true);
    }

    private void cargarReporte() {
        reporteActual = sistema.paquetesPorEstado();
        DefaultTableModel modeloTabla = (DefaultTableModel) tablaReporte.getModel();
        modeloTabla.setRowCount(0);
        for (Zona zona : Zona.values()) {
            modeloTabla.addRow(new Object[]{
                zona,
                reporteActual.getCantidad(zona, EstadoPaquete.PENDIENTE),
                reporteActual.getCantidad(zona, EstadoPaquete.ENVIADO),
                reporteActual.getCantidad(zona, EstadoPaquete.RECIBIDO),
                reporteActual.getTotal(zona)
            });
        }
    }

    private void mostrarDetalleCeldaSeleccionada() {
        int filaVista = tablaReporte.getSelectedRow();
        int columnaVista = tablaReporte.getSelectedColumn();
        if (filaVista < 0 || columnaVista < 0) {
            limpiarDetalle();
            return;
        }
        int filaModelo = tablaReporte.convertRowIndexToModel(filaVista);
        int columnaModelo = tablaReporte.convertColumnIndexToModel(columnaVista);
        if (columnaModelo == 0) {
            limpiarDetalle();
            return;
        }
        Zona zona = (Zona) tablaReporte.getModel().getValueAt(filaModelo, 0);
        EstadoPaquete estado = estadoPorColumna(columnaModelo);
        DetallePaquetesPorEstado detalle;
        if (estado == null) {
            detalle = sistema.detallePaquetesPorZona(zona);
        } else {
            detalle = sistema.detallePaquetesPorEstado(zona, estado);
        }
        mostrarDetalle(zona, estado, detalle);
    }

    private void mostrarDetalle(
            Zona zona,
            EstadoPaquete estado,
            DetallePaquetesPorEstado detalle) {
        StringBuilder texto = new StringBuilder();
        texto.append("Zona: ").append(zona).append("\n");
        if (estado == null) {
            texto.append("Estado: TOTAL").append("\n");
        } else {
            texto.append("Estado: ").append(estado).append("\n");
        }
        texto.append("Clientes diferentes: ")
                .append(detalle.getCantidadClientesDiferentes())
                .append("\n\n");
        texto.append("Departamentos destino:").append("\n");
        if (detalle.getDepartamentosDestino().isEmpty()) {
            texto.append("- Sin departamentos");
        } else {
            for (Departamento departamento : detalle.getDepartamentosDestino()) {
                texto.append("- ").append(departamento).append("\n");
            }
        }
        txtDetalle.setText(texto.toString());
        txtDetalle.setCaretPosition(0);
    }

    private void limpiarDetalle() {
        txtDetalle.setText(
                "Seleccione una celda de la tabla para ver el detalle.\n\n"
                + "Columnas disponibles:\n"
                + "- Pendiente\n"
                + "- Enviado\n"
                + "- Recibido\n"
                + "- Total"
        );
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String evento = evt.getPropertyName();
        if (TipoEvento.PAQUETES.name().equals(evento)
                || TipoEvento.ENVIOS.name().equals(evento)
                || TipoEvento.RECEPCIONES.name().equals(evento)
                || TipoEvento.TODO.name().equals(evento)) {
            actualizarDatos();
        }
    }

    @Override
    public void dispose() {
        sistema.removePropertyChangeListener(this);
        super.dispose();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnCerrar = new javax.swing.JButton();
        btnActualizar = new javax.swing.JButton();
        scrollReporte = new javax.swing.JScrollPane();
        tablaReporte = new javax.swing.JTable();
        scrollDetalle = new javax.swing.JScrollPane();
        txtDetalle = new javax.swing.JTextArea();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        lblMapa = new javax.swing.JLabel();
        lblOeste = new javax.swing.JLabel();
        lblNorte = new javax.swing.JLabel();
        lblEste = new javax.swing.JLabel();
        lblSur = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Reporte de paquetes por estado");

        btnCerrar.setText("Cerrar");
        btnCerrar.addActionListener(this::btnCerrarActionPerformed);

        btnActualizar.setText("Actualizar");
        btnActualizar.addActionListener(this::btnActualizarActionPerformed);

        tablaReporte.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tablaReporte.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tablaReporteMouseClicked(evt);
            }
        });
        scrollReporte.setViewportView(tablaReporte);

        txtDetalle.setColumns(20);
        txtDetalle.setRows(5);
        scrollDetalle.setViewportView(txtDetalle);

        lblMapa.setText("jLabel1");

        lblOeste.setBackground(new java.awt.Color(255, 255, 0));
        lblOeste.setForeground(new java.awt.Color(0, 0, 0));
        lblOeste.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblOeste.setText("Oeste");
        lblOeste.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblOeste.setOpaque(true);
        lblOeste.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblOesteMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblOesteMouseExited(evt);
            }
        });

        lblNorte.setBackground(new java.awt.Color(255, 255, 0));
        lblNorte.setForeground(new java.awt.Color(0, 0, 0));
        lblNorte.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblNorte.setText("Oeste");
        lblNorte.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblNorte.setOpaque(true);
        lblNorte.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblNorteMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblNorteMouseExited(evt);
            }
        });

        lblEste.setBackground(new java.awt.Color(255, 255, 0));
        lblEste.setForeground(new java.awt.Color(0, 0, 0));
        lblEste.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblEste.setText("Oeste");
        lblEste.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblEste.setOpaque(true);
        lblEste.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblEsteMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblEsteMouseExited(evt);
            }
        });

        lblSur.setBackground(new java.awt.Color(255, 255, 0));
        lblSur.setForeground(new java.awt.Color(0, 0, 0));
        lblSur.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblSur.setText("Oeste");
        lblSur.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        lblSur.setOpaque(true);
        lblSur.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                lblSurMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                lblSurMouseExited(evt);
            }
        });

        jLayeredPane1.setLayer(lblMapa, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(lblOeste, javax.swing.JLayeredPane.PALETTE_LAYER);
        jLayeredPane1.setLayer(lblNorte, javax.swing.JLayeredPane.PALETTE_LAYER);
        jLayeredPane1.setLayer(lblEste, javax.swing.JLayeredPane.PALETTE_LAYER);
        jLayeredPane1.setLayer(lblSur, javax.swing.JLayeredPane.PALETTE_LAYER);

        javax.swing.GroupLayout jLayeredPane1Layout = new javax.swing.GroupLayout(jLayeredPane1);
        jLayeredPane1.setLayout(jLayeredPane1Layout);
        jLayeredPane1Layout.setHorizontalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jLayeredPane1Layout.createSequentialGroup()
                        .addGap(84, 84, 84)
                        .addComponent(lblOeste)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblMapa, javax.swing.GroupLayout.PREFERRED_SIZE, 201, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblEste))
                    .addGroup(jLayeredPane1Layout.createSequentialGroup()
                        .addGap(201, 201, 201)
                        .addComponent(lblNorte))
                    .addGroup(jLayeredPane1Layout.createSequentialGroup()
                        .addGap(201, 201, 201)
                        .addComponent(lblSur)))
                .addContainerGap(78, Short.MAX_VALUE))
        );
        jLayeredPane1Layout.setVerticalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jLayeredPane1Layout.createSequentialGroup()
                .addContainerGap(23, Short.MAX_VALUE)
                .addComponent(lblNorte)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblMapa, javax.swing.GroupLayout.PREFERRED_SIZE, 203, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblOeste)
                    .addComponent(lblEste))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblSur)
                .addGap(18, 18, 18))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(287, 287, 287)
                        .addComponent(btnActualizar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnCerrar))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(scrollReporte, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(scrollDetalle)
                            .addComponent(jLayeredPane1))
                        .addGap(0, 0, Short.MAX_VALUE))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(scrollReporte, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(scrollDetalle, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnActualizar)
                    .addComponent(btnCerrar))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tablaReporteMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tablaReporteMouseClicked
        mostrarDetalleCeldaSeleccionada();
    }//GEN-LAST:event_tablaReporteMouseClicked

    private void btnActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActualizarActionPerformed
        actualizarDatos();
    }//GEN-LAST:event_btnActualizarActionPerformed

    private void btnCerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarActionPerformed
        dispose();
    }//GEN-LAST:event_btnCerrarActionPerformed

    private void lblOesteMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblOesteMouseEntered
        marcarZonaSeleccionada(lblOeste);
        mostrarVentanaZona(Zona.OESTE);
    }//GEN-LAST:event_lblOesteMouseEntered

    private void lblNorteMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblNorteMouseEntered
        marcarZonaSeleccionada(lblNorte);
        mostrarVentanaZona(Zona.NORTE);
    }//GEN-LAST:event_lblNorteMouseEntered

    private void lblEsteMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblEsteMouseEntered
        marcarZonaSeleccionada(lblEste);
        mostrarVentanaZona(Zona.ESTE);
    }//GEN-LAST:event_lblEsteMouseEntered

    private void lblSurMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblSurMouseEntered
        marcarZonaSeleccionada(lblSur);
        mostrarVentanaZona(Zona.SUR);
    }//GEN-LAST:event_lblSurMouseEntered

    private void lblNorteMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblNorteMouseExited
        desmarcarZona(lblNorte);
    }//GEN-LAST:event_lblNorteMouseExited

    private void lblEsteMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblEsteMouseExited
        desmarcarZona(lblEste);
    }//GEN-LAST:event_lblEsteMouseExited

    private void lblOesteMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblOesteMouseExited
        desmarcarZona(lblOeste);
    }//GEN-LAST:event_lblOesteMouseExited

    private void lblSurMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lblSurMouseExited
        desmarcarZona(lblSur);
    }//GEN-LAST:event_lblSurMouseExited

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActualizar;
    private javax.swing.JButton btnCerrar;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JLabel lblEste;
    private javax.swing.JLabel lblMapa;
    private javax.swing.JLabel lblNorte;
    private javax.swing.JLabel lblOeste;
    private javax.swing.JLabel lblSur;
    private javax.swing.JScrollPane scrollDetalle;
    private javax.swing.JScrollPane scrollReporte;
    private javax.swing.JTable tablaReporte;
    private javax.swing.JTextArea txtDetalle;
    // End of variables declaration//GEN-END:variables
}
