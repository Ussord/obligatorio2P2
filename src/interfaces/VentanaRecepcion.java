/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package interfaces;

import interfaces.renderers.EnvioCellRenderer;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import modelo.Envio;
import modelo.Paquete;
import modelo.SistemaEnvios;
import modelo.enums.TipoEvento;

/**
 *
 * @author Mauro Russo
 */
public class VentanaRecepcion extends JFrame implements PropertyChangeListener {

    private static final DateTimeFormatter FORMATO_FECHA
            = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final SistemaEnvios sistema;

    /**
     * Creates new form VentanaRecepcion
     */
    public VentanaRecepcion(SistemaEnvios sistema) {
        if (sistema == null) {
            throw new IllegalArgumentException("El sistema no puede ser null.");
        }
        this.sistema = sistema;
        initComponents();
        setTitle("Recepción de envíos");
        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        sistema.addPropertyChangeListener(this);
        configurarListas();
        cargarEnvios();
        limpiarSeleccion();
    }

    private void cargarEnvios() {
        Envio seleccionado = lstEnvios.getSelectedValue();
        DefaultListModel<Envio> modeloLista = new DefaultListModel<>();
        for (Envio envio : sistema.enviosOrdenadosPorNumeroDecreciente()) {
            modeloLista.addElement(envio);
        }
        lstEnvios.setModel(modeloLista);
        if (seleccionado != null) {
            seleccionarEnvio(seleccionado);
        }
    }

    private void cargarDatosEnvioSeleccionado() {
        Envio envio = lstEnvios.getSelectedValue();
        if (envio == null) {
            limpiarDatosEnvio();
            return;
        }
        lblNumero.setText(String.valueOf(envio.getNumero()));
        lblFechaEnvio.setText(envio.getFechaEnvio().format(FORMATO_FECHA));
        lblZona.setText(String.valueOf(envio.getZona()));
        lblFuncionario.setText(envio.getFuncionario().getNombre());
        if (envio.tieneRecepcionRegistrada()) {
            lblEstadoRecepcion.setText("Recepcionado");
            btnConfirmarRecepcion.setEnabled(false);
            lstPaquetes.setEnabled(false);
        } else {
            lblEstadoRecepcion.setText("Pendiente de recepción");
            btnConfirmarRecepcion.setEnabled(true);
            lstPaquetes.setEnabled(true);
        }
        cargarPaquetesDelEnvio(envio);
    }

    private void cargarPaquetesDelEnvio(Envio envio) {
        DefaultListModel<Paquete> modeloLista = new DefaultListModel<>();
        for (Paquete paquete : envio.getPaquetes()) {
            modeloLista.addElement(paquete);
        }
        lstPaquetes.setModel(modeloLista);
        lstPaquetes.clearSelection();
    }

    private void confirmarRecepcion() {
        Envio envio = lstEnvios.getSelectedValue();
        if (envio == null) {
            mostrarError("Debe seleccionar un envío.");
            return;
        }
        if (envio.tieneRecepcionRegistrada()) {
            mostrarError("El envío seleccionado ya fue recepcionado.");
            return;
        }
        int opcion = JOptionPane.showConfirmDialog(
                this,
                "Los paquetes seleccionados quedarán como recibidos. "
                + "Los no seleccionados volverán a pendiente. ¿Desea continuar?",
                "Confirmar recepción",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );
        if (opcion != JOptionPane.YES_OPTION) {
            return;
        }
        try {
            ArrayList<Paquete> paquetesEntregados
                    = new ArrayList<>(lstPaquetes.getSelectedValuesList());
            sistema.registrarRecepcion(envio, paquetesEntregados);
            JOptionPane.showMessageDialog(
                    this,
                    "Recepción registrada correctamente.",
                    "Recepción",
                    JOptionPane.INFORMATION_MESSAGE
            );
            actualizarDatos();
        } catch (IllegalArgumentException | IllegalStateException e) {
            mostrarError(e.getMessage());
        }
    }

    private void limpiarSeleccion() {
        lstEnvios.clearSelection();
        limpiarDatosEnvio();
    }

    private void limpiarDatosEnvio() {
        lblNumero.setText("-");
        lblFechaEnvio.setText("-");
        lblZona.setText("-");
        lblFuncionario.setText("-");
        lblEstadoRecepcion.setText("-");
        lstPaquetes.setModel(new DefaultListModel<>());
        lstPaquetes.setEnabled(false);
        btnConfirmarRecepcion.setEnabled(false);
    }

    private void actualizarDatos() {
        cargarEnvios();
        cargarDatosEnvioSeleccionado();
    }

    private void seleccionarEnvio(Envio envio) {
        DefaultListModel<Envio> modeloLista = (DefaultListModel<Envio>) lstEnvios.getModel();
        for (int i = 0; i < modeloLista.getSize(); i++) {
            if (modeloLista.getElementAt(i) == envio) {
                lstEnvios.setSelectedIndex(i);
                return;
            }
        }
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(
                this,
                mensaje,
                "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblEnvios = new javax.swing.JLabel();
        scrollEnvios = new javax.swing.JScrollPane();
        lstEnvios = new javax.swing.JList<>();
        lblPaquetes = new javax.swing.JLabel();
        scrollPaquetes = new javax.swing.JScrollPane();
        lstPaquetes = new javax.swing.JList<>();
        lblDatos = new javax.swing.JLabel();
        lblNumeroTitulo = new javax.swing.JLabel();
        lblFechaEnvioTitulo = new javax.swing.JLabel();
        lblZonaTitulo = new javax.swing.JLabel();
        lblFuncionarioTitulo = new javax.swing.JLabel();
        lblEstadoRecepcionTitulo = new javax.swing.JLabel();
        lblNumero = new javax.swing.JLabel();
        lblFechaEnvio = new javax.swing.JLabel();
        lblZona = new javax.swing.JLabel();
        lblFuncionario = new javax.swing.JLabel();
        lblEstadoRecepcion = new javax.swing.JLabel();
        btnCerrar = new javax.swing.JButton();
        btnLimpiar = new javax.swing.JButton();
        btnConfirmarRecepcion = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Recepción de envíos");

        lblEnvios.setText("Envíos");

        lstEnvios.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        lstEnvios.addListSelectionListener(this::lstEnviosValueChanged);
        scrollEnvios.setViewportView(lstEnvios);

        lblPaquetes.setText("Paquetes del envío");

        scrollPaquetes.setViewportView(lstPaquetes);

        lblDatos.setText("Datos:");

        lblNumeroTitulo.setText("Número:");

        lblFechaEnvioTitulo.setText("Fecha de envío:");

        lblZonaTitulo.setText("Zona:");

        lblFuncionarioTitulo.setText("Funcionario:");

        lblEstadoRecepcionTitulo.setText("Estado:");

        lblNumero.setText("jLabel6");

        lblFechaEnvio.setText("jLabel6");

        lblZona.setText("jLabel6");

        lblFuncionario.setText("jLabel6");

        lblEstadoRecepcion.setText("jLabel6");

        btnCerrar.setText("Cerrar");
        btnCerrar.addActionListener(this::btnCerrarActionPerformed);

        btnLimpiar.setText("Limpiar");
        btnLimpiar.addActionListener(this::btnLimpiarActionPerformed);

        btnConfirmarRecepcion.setText("Confirmar");
        btnConfirmarRecepcion.addActionListener(this::btnConfirmarRecepcionActionPerformed);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblEnvios)
                            .addComponent(scrollEnvios, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblPaquetes)
                            .addComponent(scrollPaquetes, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(28, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(lblDatos, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblFechaEnvioTitulo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblZonaTitulo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblFuncionarioTitulo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblEstadoRecepcionTitulo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblNumeroTitulo, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblNumero)
                            .addComponent(lblFechaEnvio)
                            .addComponent(lblZona)
                            .addComponent(lblFuncionario)
                            .addComponent(lblEstadoRecepcion))
                        .addGap(0, 0, Short.MAX_VALUE))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnConfirmarRecepcion)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnLimpiar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCerrar)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblEnvios)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(scrollEnvios))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(lblPaquetes)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(scrollPaquetes, javax.swing.GroupLayout.PREFERRED_SIZE, 284, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(30, 30, 30)
                .addComponent(lblDatos)
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblNumeroTitulo)
                    .addComponent(lblNumero))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFechaEnvioTitulo)
                    .addComponent(lblFechaEnvio))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblZonaTitulo)
                    .addComponent(lblZona))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFuncionarioTitulo)
                    .addComponent(lblFuncionario))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEstadoRecepcionTitulo)
                    .addComponent(lblEstadoRecepcion))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCerrar)
                    .addComponent(btnLimpiar)
                    .addComponent(btnConfirmarRecepcion))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnConfirmarRecepcionActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmarRecepcionActionPerformed
        confirmarRecepcion();
    }//GEN-LAST:event_btnConfirmarRecepcionActionPerformed

    private void btnLimpiarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLimpiarActionPerformed
        limpiarSeleccion();
    }//GEN-LAST:event_btnLimpiarActionPerformed

    private void btnCerrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCerrarActionPerformed
        dispose();
    }//GEN-LAST:event_btnCerrarActionPerformed

    private void lstEnviosValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_lstEnviosValueChanged
        if (!evt.getValueIsAdjusting()) {
            cargarDatosEnvioSeleccionado();
        }
    }//GEN-LAST:event_lstEnviosValueChanged

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String evento = evt.getPropertyName();
        if (TipoEvento.FUNCIONARIOS.name().equals(evento)
                || TipoEvento.ENVIOS.name().equals(evento)
                || TipoEvento.PAQUETES.name().equals(evento)
                || TipoEvento.RECEPCIONES.name().equals(evento)) {
            actualizarDatos();
        }
    }

    @Override
    public void dispose() {
        sistema.removePropertyChangeListener(this);
        super.dispose();
    }

    private void configurarListas() {
        lstEnvios.setCellRenderer(new EnvioCellRenderer());
        lstPaquetes.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCerrar;
    private javax.swing.JButton btnConfirmarRecepcion;
    private javax.swing.JButton btnLimpiar;
    private javax.swing.JLabel lblDatos;
    private javax.swing.JLabel lblEnvios;
    private javax.swing.JLabel lblEstadoRecepcion;
    private javax.swing.JLabel lblEstadoRecepcionTitulo;
    private javax.swing.JLabel lblFechaEnvio;
    private javax.swing.JLabel lblFechaEnvioTitulo;
    private javax.swing.JLabel lblFuncionario;
    private javax.swing.JLabel lblFuncionarioTitulo;
    private javax.swing.JLabel lblNumero;
    private javax.swing.JLabel lblNumeroTitulo;
    private javax.swing.JLabel lblPaquetes;
    private javax.swing.JLabel lblZona;
    private javax.swing.JLabel lblZonaTitulo;
    private javax.swing.JList<Envio> lstEnvios;
    private javax.swing.JList<Paquete> lstPaquetes;
    private javax.swing.JScrollPane scrollEnvios;
    private javax.swing.JScrollPane scrollPaquetes;
    // End of variables declaration//GEN-END:variables

}
